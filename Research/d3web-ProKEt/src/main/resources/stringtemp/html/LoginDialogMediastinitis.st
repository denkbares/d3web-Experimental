<div id="jqLoginDialog" title="Bitte geben Sie Ihre LOGIN Daten ein:">

		<table>
			<tbody>
			<tr>
				<td>
				<table>
					<tbody>
						<tr>
						<td width="60px;">Benutzer:</td>
						<td><input id="usrInput" /></td> 
						</tr>
						<tr>
						<td width="60px;">Passwort</td>
						<td><input id="pwInput" type="password"/></td> 
						</tr>
					</tbody>
				</table>
				</td>
				<td>
					<img src="kbimg/logo.png" alt="logo" width="70px;"/>
				</td>
			</tr>
			</tbody>
		</table>
		
		
		<div style="margin-top:5px; margin-bottom:5px;" id="loginError"></div>
		<div style="margin-top:5px; margin-bottom:15px; cursor:pointer;" id="loginErrorMailRequest" 
			onclick='javascript:sendLoginMailRequest();'></div>
		
		
		<div style="margin-top:10px;" id="loginExplanation">
		<b>Hinweis:</b> Sie werden nach <b>60 Minuten Inaktivität</b> automatisch ausgeloggt.<br /><br />
		
		Wenn Sie den Fall nicht explizit mit <b>Fall speichern</b> abgespeichert hatten, können Sie
		den letzten Bearbeitungsstand mit <b>Fall laden (autosave)</b> wiederherstellen.
		In der Datenbank wird der Fall aber nur dauerhaft übernommen, wenn er mit <b>Fall speichern</b>
		unter einen expliziten Fallnamen gespeichert wurde!	<br /><br />
		Bereits eingegebene und abgespeicherte Fälle können Sie mittels <b>Fall laden (Fallname)</b> weiterbearbeiten.
		</div>
</div>