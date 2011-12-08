(function () {
  
  CodeMirror.usersupportHint = function(editor, id) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = editor.getTokenAt(cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
    if (!/^[\w$_]*$/.test(token.string)) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: token.string == "." ? "property" : null};
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "property") {
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (tprop.string != ".") return;
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (!context) var context = [];
      context.push(tprop);
    }
    var liste = KNOWWE.plugin.usersupport.getCompletions(token, context, id);
    return {list: liste,
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
  }
})();
