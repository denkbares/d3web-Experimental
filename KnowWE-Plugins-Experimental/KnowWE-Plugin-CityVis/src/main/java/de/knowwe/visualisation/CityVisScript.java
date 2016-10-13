package de.knowwe.visualisation;


import de.knowwe.core.compile.DefaultGlobalCompiler;
import de.knowwe.core.kdom.basicType.AttachmentType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.CompilerMessage;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;


/**
 * Created by Lea on 26.07.2016.
 */
public class CityVisScript extends DefaultGlobalCompiler.DefaultGlobalScript<CityVisType> {

    @Override
    public void compile(DefaultGlobalCompiler compiler, Section<CityVisType> section) throws CompilerMessage {
        Section<? extends AnnotationContentType> annotationSection = DefaultMarkupType.getAnnotationContentSection(section, CityVisType.ANNOTATION_IMPORT);
        Section<AttachmentType> attachmentSection = Sections.successor(annotationSection, AttachmentType.class);
        WikiAttachment attachment;
        try {
            attachment = AttachmentType.getAttachment(attachmentSection);
            attachment.getFileName();

        } catch (IOException e) {
            throw CompilerMessage.error("Exception while retrieving attachment");
        }
    }
}
