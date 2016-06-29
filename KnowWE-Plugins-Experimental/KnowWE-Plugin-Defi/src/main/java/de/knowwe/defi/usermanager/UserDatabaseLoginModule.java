/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.defi.usermanager;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.wiki.auth.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiPrincipal;
import org.apache.wiki.auth.login.AbstractLoginModule;
import org.apache.wiki.auth.login.UserDatabaseCallback;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;

import de.d3web.utils.Log;

/**
 * <p>
 * Logs in a user based on a username, password, and static password file
 * location. This module must be used with a CallbackHandler (such as
 * {@link org.apache.wiki.auth.login.WikiCallbackHandler}) that supports the following Callback types:
 * </p>
 * <ol>
 * <li>{@link javax.security.auth.callback.NameCallback}- supplies the
 * username</li>
 * <li>{@link javax.security.auth.callback.PasswordCallback}- supplies the
 * password</li>
 * <li>{@link org.apache.wiki.auth.login.UserDatabaseCallback}- supplies the
 * {@link org.apache.wiki.auth.user.UserDatabase}</li>
 * </ol>
 * <p>
 * After authentication, a Principals based on the login name will be created
 * and associated with the Subject.
 * </p>
 * @since 2.3
 */
public class UserDatabaseLoginModule extends AbstractLoginModule
{

    /**
     * @see javax.security.auth.spi.LoginModule#login()
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean login() throws LoginException
    {
        UserDatabaseCallback ucb = new UserDatabaseCallback();
        NameCallback ncb = new NameCallback( "User name" );
        PasswordCallback pcb = new PasswordCallback( "Password", false );
        Callback[] callbacks = new Callback[]
        { ucb, ncb, pcb };
        try
        {
            m_handler.handle( callbacks );
            UserDatabase db = ucb.getUserDatabase();
            String username = ncb.getName();
            String password = new String( pcb.getPassword() );

            // Look up the user and compare the password hash
            if ( db == null )
            {
                throw new FailedLoginException( "No user database: check the callback handler code!" );
            }
            UserProfile profile = db.findByLoginName( username );
            if (profile.isLocked()) {
                throw new FailedLoginException( "The profile is locked. Please contact the administrator." );
            }

            String storedPassword = profile.getPassword();
            if ( storedPassword != null && db.validatePassword( username, password ) )
            {
                Log.finest("Logged in user database user " + username);

                // If login succeeds, commit these principals/roles
                m_principals.add( new WikiPrincipal( username,  WikiPrincipal.LOGIN_NAME ) );

                return true;
            }
            throw new FailedLoginException( "The username or password is incorrect." );
        }
        catch( IOException e )
        {
            String message = "IO exception; disallowing login.";
            Log.severe( message, e );
            throw new LoginException( message );
        }
        catch( UnsupportedCallbackException e )
        {
            String message = "Unable to handle callback; disallowing login.";
            Log.severe( message, e );
            throw new LoginException( message );
        }
        catch( NoSuchPrincipalException e )
        {
            throw new FailedLoginException( "The username or password is incorrect." );
        }
    }

}
