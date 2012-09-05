/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.proket.d3web.output.render;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.database.DateCoDec;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;

/**
 * Renderer for rendering basic OCAnswers.
 *
 * TODO CHECK: 1) basic properties for answers 2) d3web resulting properties,
 * e.g. is indicated, is shown etc.
 *
 * @author Martina Freiberg @created 15.01.2011
 */
public class AnswerZCD3webRenderer extends AbstractD3webRenderer implements AnswerD3webRenderer {

    @Override
    /**
     * Specifically adapted for OCAnswer rendering
     */
    public String renderTerminologyObject(ContainerCollection cc, Session d3webSession,
            Choice c, TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession) {

        StringBuilder sb = new StringBuilder();

        // return if the InterviewObject is null
        if (to == null) {
            return "";
        }

        StringTemplate st = null;

        // if we have an image question 
        // TODO later evtl: make ProkEt property for img upload and quere here
        if (to.getName().contains("Upload images")
                || to.getName().contains("Bilder hochladen")) {
            // get the template. In case user prefix was specified, the specific
            // TemplateName is returned, otherwise the base object name.
            st = TemplateUtils.getStringTemplate(
                    super.getTemplateName("ZcAnswer_ImgUpload"), "html");

            st.setAttribute("fullId", getID(c));// .getName().replace(" ", "_"));
            st.setAttribute("realAnswerType", "oc");
            st.setAttribute("parentFullId", getID(to));// getName().replace(" ",
            st.setAttribute("title", D3webUtils.getTOPrompt(to, loc));
            // "_"));

            // set the tooltip if exists
            String resString = c.getInfoStore().getValue(ProKEtProperties.POPUP);
            if (resString != null) {
                st.setAttribute("tooltip", resString);
            }

            // assemble link here
            StringBuilder linkBui = new StringBuilder();
            linkBui.append("http://promotion.medizin.uni-wuerzburg.de/idb/index.jsp");
            linkBui.append("?ext=eurahs");
            
            String token = DateCoDec.getCode();
            linkBui.append("&t=" + token);  // TODO: get Token
            
            String mailPure = "";
            if(httpSession.getAttribute("user")!=null){
                mailPure = httpSession.getAttribute("user").toString();
            } else {
                mailPure = "default@mymail.com"; // just a default for null cases
            }
            String mailEncoded = Base64.encodeBase64String(mailPure.getBytes());
            linkBui.append("&l=" + mailEncoded);  // TODO: url-encoded mail address


            st.setAttribute("text", linkBui.toString());
            st.setAttribute("count", D3webConnector.getInstance().getTOCount(to));
        }


        sb.append(st.toString());

        super.makeTables(c, to, cc, sb);

        return sb.toString();
    }
}
