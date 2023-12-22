package de.skyrising.aoc.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*

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
                    val parts = mutableListOf<String>()
                    for (decl in file.declarations) {
                        if (decl !is KSFunctionDeclaration) continue
                        if (!decl.simpleName.asString().startsWith("part")) continue
                        val rec = decl.extensionReceiver?.element as? KSClassifierReference ?: continue
                        if (rec.referencedName() != "PuzzleInput") continue
                        parts.add(decl.simpleName.asString())
                    }
                    val benchClassName = ClassName(packageName, "BenchmarkDay$day")
                    val fileSpec = FileSpec.builder(benchClassName)
                        .apply {
                            for (part in parts) addImport(file.packageName.asString(), part)
                        }
                        .addType(TypeSpec.classBuilder(benchClassName)
                            .superclass(ClassName("de.skyrising.aoc", "BenchmarkBaseBase"))
                            .addSuperclassConstructorParameter("%L", year)
                            .addSuperclassConstructorParameter("%L", day)
                            .apply {
                                for (part in parts) {
                                    addFunction(FunSpec.builder(part)
                                        .addAnnotation(ClassName("kotlinx.benchmark", "Benchmark"))
                                        .returns(Any::class.asClassName().copy(nullable = true))
                                        .addStatement("return input.%N()", part)
                                        .build())
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