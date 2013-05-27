package de.knowwe.ophtovisD3;

import java.io.IOException;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.ophtovisD3.GraphBuilder;


public class AjaxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		
		GraphBuilder builder = new GraphBuilder();
		String type ="";
		type = context.getParameter("type");
		if(type==null)
			type="";
		String responseString ="";
		if(type.equals("force")){
			responseString =builder.bulidNamesandConnectionsJSON("Anamnese_Patientensituation", "unterkonzept");
		}else if((type.equals("bubble"))){
			 responseString = GraphBuilder.buildGraph("Anamnese_Patientensituation", "unterkonzept", "temporalGraph",true);
		}else{
			 responseString = GraphBuilder.buildGraph("Anamnese_Patientensituation", "unterkonzept", "temporalGraph",false);
		}

		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(responseString);
		
	
		
	}

}
