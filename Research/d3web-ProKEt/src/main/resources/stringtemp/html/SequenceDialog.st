$HTMLHeader()$

<body>
<div id="head">
	$header$
</div>

<div id="middle">
	<div id="content">
		<div class="dialog">
			$if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$$endif$
	
			<table>
			  <tr>
			  	<td>
				<div id="dialog-content">
				$children$			
				</div>
			  </td>
			 
			</td>
			</tr>
		  </table>
		</div>
	</div>
  </div>

</body></html>