package de.knowwe.visualisation;

/**
 * Created by Lea on 26.07.2016.
 */
import de.knowwe.core.Environment;
import de.knowwe.core.compile.CompileScript;
import de.knowwe.core.compile.Compiler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.packaging.DefaultMarkupPackageCompileTypeRenderer;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.AttachmentType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.CompilerMessage;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.util.Icon;

import java.io.IOException;
import java.util.Collection;


public class CityVisType extends DefaultMarkupType {
    public static final String PLUGIN_ID = "KnowWE-Plugin";

    public static final String ANNOTATION_IMPORT = "import";

    public static final DefaultMarkup MARKUP;

    static {
        MARKUP = new DefaultMarkup("CityVis");
        MARKUP.addAnnotation(ANNOTATION_IMPORT, false);
        MARKUP.addAnnotationIcon(ANNOTATION_IMPORT, Icon.FILE.addTitle("Import"));
        MARKUP.addAnnotationContentType(ANNOTATION_IMPORT, new AttachmentType());
    }

    public CityVisType(){
        super(MARKUP);

        this.addCompileScript(Priority.LOWER, new CityVisScript());
        this.setRenderer(new DefaultMarkupPackageCompileTypeRenderer() {
            @Override
            public void renderContents(Section<?> section, UserContext user, RenderResult result)  {
                Section<? extends AnnotationContentType> annotationSection = DefaultMarkupType.getAnnotationContentSection(section, CityVisType.ANNOTATION_IMPORT);
                Section<AttachmentType> attachmentSection = Sections.successor(annotationSection, AttachmentType.class);
                WikiAttachment attachment = null;
                try {
                    attachment = AttachmentType.getAttachment(attachmentSection);
                } catch (IOException e) {
                    Messages.error("Could not retrieve attachment");
                }
                String fileName = attachment.getFileName();
                System.out.println(fileName);
                String path = "attach/"+section.getArticle().getTitle()+"/"+fileName;
                String sectionID = section.getID();

                result.appendHtml("<script>KNOWWE.plugin.cityVis.render('"+path+"', '"+sectionID+"')</script>");
            }
        });
    }

    @Override
    public <C extends Compiler, T extends Type> void addCompileScript(CompileScript<C, T> script) {
        super.addCompileScript(script);

    }
}
