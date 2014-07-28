<%@ page import="org.apache.log4j.*"%>
<%@ page import="org.apache.wiki.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="javax.mail.*"%>
<%@ page errorPage="/Error.jsp"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.*"%>
<%!

Logger log = Logger.getLogger( "JSPWiki" );

    String message = null;

    public boolean createAccount( WikiEngine wiki, HttpServletRequest request, ResourceBundle rb )
    {
        // Insert new account for CaseTrain-Wiki-Users
        String name = request.getParameter( "name" );

        UserDatabase userDatabase = wiki.getUserManager().getUserDatabase();
        boolean success = false;
        
        String key = ResourceBundle.getBundle("casetrain-plugin-config").getString("accessKey");
        
        Integer pwInt = Math.abs(name.hashCode() + key.hashCode());
        String pw = pwInt.toString();
        System.out.println(pw);
        
        try
        	{
        
       	 	UserProfile newProfile = userDatabase.newProfile();
	     	newProfile.setLoginName(name);
       	 	newProfile.setFullname(name);
      		newProfile.setPassword(pw);
       		userDatabase.save(newProfile);
        
        	return true;
        	} catch( WikiSecurityException e )
        {
            log.error( "Tried to create account, but: " + e );
        }
        return false;
        /*
	String[] nameArray = names.split(";");
	for (int i = 0; i < nameArray.length; i++) {
	String name = nameArray[i];
        try
        {
            UserProfile profile = null;
            
             // This is disabled because it would otherwise be possible to DOS JSPWiki instances
             // by requesting new passwords for all users.  See https://issues.apache.org/jira/browse/JSPWIKI-78
             try
             {
             profile = userDatabase.find(name);
             }
             catch (NoSuchPrincipalException e)
             {
             // Try email as well
             }
             
            if( profile == null )
            {
                profile = userDatabase.findByEmail( name );
            }

            String email = profile.getEmail();

            String randomPassword = TextUtil.generateRandomPassword();

            // Try sending email first, as that is more likely to fail.

            Object[] args = { profile.getLoginName(), randomPassword,
                             wiki.getURLConstructor().makeURL( WikiContext.NONE, "Login.jsp", true, "" ), wiki.getApplicationName() };

            String mailMessage = MessageFormat.format( rb.getString( "lostpwd.newpassword.email" ), args );

            Object[] args2 = { wiki.getApplicationName() };
            MailUtil.sendMessage( wiki, email, MessageFormat.format( rb.getString( "lostpwd.newpassword.subject" ), args2 ),
                                  mailMessage );

            log.info( "User " + email + " requested and received a new password." );

            // Mail succeeded.  Now reset the password.
            // If this fails, we're kind of screwed, because we already emailed.
            profile.setPassword( randomPassword );
            userDatabase.save( profile );
            userDatabase.commit();
            success = true;
        }
        catch( NoSuchPrincipalException e )
        {
            Object[] args = { name };
            message = MessageFormat.format( rb.getString( "lostpwd.nouser" ), args );
            log.info( "Tried to reset password for non-existent user '" + name + "'" );
        }
        catch( SendFailedException e )
        {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got SendFailedException: " + e );
        }
        catch( AuthenticationFailedException e )
        {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got AuthenticationFailedException: " + e );
        }
        catch( Exception e )
        {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got another exception: " + e );
        }
	}
        return success;
        */
    }    %>
<%
    WikiEngine wiki = WikiEngine.getInstance( getServletConfig() );

    //Create wiki context like in Login.jsp:
    //don't check for access permissions: if you have lost your password you cannot login!
    WikiContext wikiContext = (WikiContext) pageContext.getAttribute( WikiTagBase.ATTR_CONTEXT, PageContext.REQUEST_SCOPE );

    // If no context, it means we're using container auth.  So, create one anyway
    if( wikiContext == null )
    {
        wikiContext = wiki.createContext( request, WikiContext.LOGIN ); /* reuse login context ! */
        pageContext.setAttribute( WikiTagBase.ATTR_CONTEXT, wikiContext, PageContext.REQUEST_SCOPE );
    }

    ResourceBundle rb = wikiContext.getBundle( "CoreResources" );

    WikiSession wikiSession = wikiContext.getWikiSession();
    String action = request.getParameter( "action" );

    boolean done = false;

            if( createAccount( wiki, request, rb ) )
        {
            done = true;
            wikiSession.addMessage( "resetpwok", rb.getString( "lostpwd.emailed" ) );
            pageContext.setAttribute( "passwordreset", "done" );
        }
        else
        // Error
        {
            wikiSession.addMessage( "resetpw", message );
        }
    

    response.setContentType( "text/html; charset=" + wiki.getContentEncoding() );
    response.setHeader( "Cache-control", "max-age=0" );
    response.setDateHeader( "Expires", new Date().getTime() );
    response.setDateHeader( "Last-Modified", new Date().getTime() );

    String contentPage = wiki.getTemplateManager().findJSP( pageContext, wikiContext.getTemplate(), "ViewTemplate.jsp" );
%>
<wiki:Include page="<%=contentPage%>" />
