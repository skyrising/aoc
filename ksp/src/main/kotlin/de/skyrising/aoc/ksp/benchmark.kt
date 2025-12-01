package de.skyrising.aoc.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*

val KSType.isVisualization: Boolean
    get() = declaration.qualifiedName?.getQualifier() == "de.skyrising.aoc.visualization" && declaration.qualifiedName?.getShortName() == "Visualization"

fun KSType.asClassName(): TypeName {
    val qName = declaration.qualifiedName ?: return ClassName("kotlin", "Any").copy(nullable = true)
    return ClassName(qName.getQualifier(), qName.getShortName()).copy(nullable = isMarkedNullable)
}

class BenchmarkProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val packageName = environment.options["aoc-year-package"] ?: error("Missing aoc-year-package option")
        val year = packageName.substring(packageName.length - 4).toInt()
        return object : SymbolProcessor {
            override fun process(resolver: Resolver): List<KSAnnotated> {
                for (file in resolver.getNewFiles()) {
                    if (file.fileName != "solution.kt") continue
                    val pkg = file.packageName.asString()
                    if (!pkg.startsWith("$packageName.day")) continue
                    val day = pkg.substring(packageName.length + 4).toInt()
                    val parts = mutableMapOf<String, KSType>()
                    var prepared: KSType? = null
                    for (decl in file.declarations) {
                        if (decl !is KSFunctionDeclaration) continue
                        val rec = decl.extensionReceiver?.element as? KSClassifierReference ?: continue
                        if (decl.simpleName.asString() == "prepare" && rec.referencedName() == "PuzzleInput") {
                            prepared = decl.returnType!!.resolve()
                            continue
                        }
                        if (!decl.simpleName.asString().startsWith("part")) continue
                        if (decl.extensionReceiver?.resolve() != prepared && rec.referencedName() != "PuzzleInput") continue
                        parts[decl.simpleName.asString()] = decl.returnType!!.resolve()
                    }
                    val benchClassName = ClassName(packageName, "BenchmarkDay$day")
                    val fileSpec = FileSpec.builder(benchClassName)
                        .apply {
                            for ((part, retType) in parts) if (!retType.isVisualization) addImport(file.packageName.asString(), part)
                            if (prepared != null) addImport(file.packageName.asString(), "prepare")
                        }
                        .addType(TypeSpec.classBuilder(benchClassName)
                            .superclass(ClassName("de.skyrising.aoc", "BenchmarkBase"))
                            .addSuperclassConstructorParameter("%L", year)
                            .addSuperclassConstructorParameter("%L", day)
                            .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "Suppress")).addMember("%S", "INAPPLICABLE_JVM_NAME").build())
                            .apply {
                                for ((part, returnType) in parts) {
                                    if (returnType.isVisualization) continue
                                    addFunction(FunSpec.builder(part).apply {
                                        addAnnotation(ClassName("kotlinx.benchmark", "Benchmark"))
                                        addAnnotation(AnnotationSpec.builder(ClassName("kotlin.jvm", "JvmName"))
                                            .addMember("%S", part)
                                            .build())
                                        //addAnnotation(AnnotationSpec.builder(ClassName("org.openjdk.jmh.annotations", "Group"))
                                        //    .addMember("%S", "aoc${year}_day${day}_$part")
                                        //    .build())
                                        //returns(ClassName("kotlin", "Any").copy(nullable = true))
                                        returns(returnType.asClassName())
                                        if (prepared != null) {
                                            addStatement("return input.prepare().%N()", part)
                                        } else {
                                            addStatement("return input.%N()", part)
                                        }
                                    }.build())
                                }
                            }
                            .build())
                        .build()

                    val fileOut = environment.codeGenerator.createNewFile(Dependencies(false, file), packageName, "BenchmarkDay$day")
                    fileOut.bufferedWriter().use {
                        fileSpec.writeTo(it)
                    }
                    environment.logger.info("$packageName.BenchmarkDay$day generated")
                }
                return emptyList()
            }
        }
    }
}
