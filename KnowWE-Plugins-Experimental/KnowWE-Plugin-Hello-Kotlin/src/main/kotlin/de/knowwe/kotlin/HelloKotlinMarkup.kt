package de.knowwe.kotlin

import de.knowwe.core.compile.packaging.PackageManager
import de.knowwe.core.kdom.AbstractType
import de.knowwe.core.kdom.sectionFinder.AllTextFinder
import de.knowwe.kdom.defaultMarkup.DefaultMarkup
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType

class HelloKotlinMarkup : DefaultMarkupType(MARKUP) {
    companion object {
        private var MARKUP: DefaultMarkup = DefaultMarkup("HelloKotlin")

        init {
            MARKUP.addContentType(KotlinContentType())
            PackageManager.addPackageAnnotation(MARKUP)
        }
    }

    class KotlinContentType : AbstractType() {
        init {
            setSectionFinder(AllTextFinder.getInstance())
            setRenderer({ _, _, result -> result.append("Hello Kotlin!!!") })
        }

    }

}
