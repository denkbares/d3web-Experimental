/* Generated By:JJTree: Do not edit this line. ASTor_op.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package de.knowwe.timeline.parser;

public
class ASTor_op extends SimpleNode {
  public ASTor_op(int id) {
    super(id);
  }

  public ASTor_op(QueryLang p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public de.knowwe.timeline.Timeset jjtAccept(QueryLangVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=395bb9acd33231bead822de79e1226da (do not edit this line) */
