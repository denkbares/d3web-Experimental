// $ANTLR 3.1 D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g 2009-01-10 19:06:18

package de.d3web.KnOfficeParser.visio;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
/**
 * Baumgrammatik um aus einer eingelesenen Visiodatei ein XML Output zu erzeugen 
 * @author Markus Friedrich
 *
 */
public class VisiotoXML extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "String", "INT", "DOT", "DD", "COMMA", "SEMI", "EX", "AT", "ORS", "NS", "TILDE", "LP", "RP", "CBO", "CBC", "SBO", "SBC", "LE", "L", "GE", "G", "EQ", "PLUS", "MINUS", "PROD", "DIV", "WS", "COMMENT", "NL", "IF", "THEN", "AND", "OR", "NOT", "HIDE", "EXCEPT", "UNKNOWN", "KNOWN", "INSTANT", "MINMAX", "IN", "ALL", "ALLOWEDNAMES", "INCLUDE", "DEFAULT", "ABSTRACT", "SET", "REF", "ID", "Page", "Shape", "Xcoord", "Ycoord", "Width", "Height", "Text", "MyDouble", "Shapetext", "Picture", "Box", "QID", "Textboxtext", "Popup", "Aidtext", "Knowledge", "Start", "End", "Pagestart", "Pagesheet", "Shapestart", "YtoWith", "HeighttoText", "Misc", "Misc2", "Misc3", "'</Pages>'", "'</Page>'", "'<Shapes>'", "'</Shapes>'", "'</PinX>'", "'<PinY>'", "'</Width>'", "'<Height>'", "'</Text></Shape>'", "'Bildname:'", "'Größe:'", "'x'", "'Frage:'", "'Folgefragen:'"
    };
    public static final int Aidtext=67;
    public static final int LP=15;
    public static final int Textboxtext=65;
    public static final int NOT=37;
    public static final int Page=53;
    public static final int EXCEPT=39;
    public static final int EOF=-1;
    public static final int DD=7;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__90=90;
    public static final int EX=10;
    public static final int INCLUDE=47;
    public static final int NL=32;
    public static final int EQ=25;
    public static final int COMMENT=31;
    public static final int Shapestart=73;
    public static final int GE=23;
    public static final int G=24;
    public static final int T__80=80;
    public static final int SBC=20;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int L=22;
    public static final int NS=13;
    public static final int KNOWN=41;
    public static final int INT=5;
    public static final int T__85=85;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__86=86;
    public static final int T__89=89;
    public static final int T__88=88;
    public static final int Picture=62;
    public static final int Xcoord=55;
    public static final int WS=30;
    public static final int OR=36;
    public static final int SBO=19;
    public static final int Misc2=77;
    public static final int Misc3=78;
    public static final int Popup=66;
    public static final int YtoWith=74;
    public static final int T__79=79;
    public static final int End=70;
    public static final int Ycoord=56;
    public static final int Shapetext=61;
    public static final int HIDE=38;
    public static final int RP=16;
    public static final int ORS=12;
    public static final int MyDouble=60;
    public static final int ABSTRACT=49;
    public static final int AND=35;
    public static final int ID=52;
    public static final int Width=57;
    public static final int IF=33;
    public static final int AT=11;
    public static final int THEN=34;
    public static final int IN=44;
    public static final int UNKNOWN=40;
    public static final int COMMA=8;
    public static final int Height=58;
    public static final int ALL=45;
    public static final int PROD=28;
    public static final int Knowledge=68;
    public static final int TILDE=14;
    public static final int PLUS=26;
    public static final int String=4;
    public static final int DOT=6;
    public static final int HeighttoText=75;
    public static final int Start=69;
    public static final int Pagesheet=72;
    public static final int ALLOWEDNAMES=46;
    public static final int Misc=76;
    public static final int INSTANT=42;
    public static final int MINMAX=43;
    public static final int DEFAULT=48;
    public static final int SET=50;
    public static final int MINUS=27;
    public static final int SEMI=9;
    public static final int REF=51;
    public static final int QID=64;
    public static final int Box=63;
    public static final int CBC=18;
    public static final int Shape=54;
    public static final int Text=59;
    public static final int DIV=29;
    public static final int CBO=17;
    public static final int LE=21;
    public static final int Pagestart=71;

    // delegates
    // delegators


        public VisiotoXML(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public VisiotoXML(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected StringTemplateGroup templateLib =
      new StringTemplateGroup("VisiotoXMLTemplates", AngleBracketTemplateLexer.class);

    public void setTemplateLib(StringTemplateGroup templateLib) {
      this.templateLib = templateLib;
    }
    public StringTemplateGroup getTemplateLib() {
      return templateLib;
    }
    /** allows convenient multi-value initialization:
     *  "new STAttrMap().put(...).put(...)"
     */
    public static class STAttrMap extends HashMap {
      public STAttrMap put(String attrName, Object value) {
        super.put(attrName, value);
        return this;
      }
      public STAttrMap put(String attrName, int value) {
        super.put(attrName, new Integer(value));
        return this;
      }
    }

    public String[] getTokenNames() { return VisiotoXML.tokenNames; }
    public String getGrammarFileName() { return "D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g"; }


      private double x,y;
      
      private int ox,oy;
      
      private double resizeX, resizeY;
      
      String picname = new String();
      String questionid = new String();
      
      private void setCorner(double x, double y, double width, double height) {
        this.x=x-(width/2);
        this.y=y+(height/2);
        resizeX=(ox/width);
        resizeY=(oy/height);
      }
      
      private long getXstart(double x, double width) {
        return Math.round((x-(width/2)-this.x)*resizeX);
      }
      
      private long getXend(double x, double width) {
        return Math.round((x+(width/2)-this.x)*resizeX);
      }
      
      private long getYstart(double y, double height) {
        return Math.round((this.y-y-(height/2))*resizeY);
      }
      
      private long getYend(double y, double height) {
        return Math.round((this.y-y+(height/2))*resizeY);
      }
      
      private String delQuotes(String s) {
        s=s.substring(1, s.length()-1);
        s=s.replace("\\\"", "\"");
        return s;
      }
      
      private String delXML(String s) {
            int i = s.indexOf('<');
            while (i>=0) {
              int j = s.indexOf(">");
              String t = s.substring(i, j+1);
              s=s.replace(t, "");
              i = s.indexOf('<');
            }
            return s;
          }


    public static class knowledge_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "knowledge"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:69:1: knowledge : ^( Knowledge (pages+= page )* ) -> foo(i=$pages);
    public final VisiotoXML.knowledge_return knowledge() throws RecognitionException {
        VisiotoXML.knowledge_return retval = new VisiotoXML.knowledge_return();
        retval.start = input.LT(1);

        List list_pages=null;
        VisiotoXML.page_return pages = null;
        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:69:11: ( ^( Knowledge (pages+= page )* ) -> foo(i=$pages))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:69:13: ^( Knowledge (pages+= page )* )
            {
            match(input,Knowledge,FOLLOW_Knowledge_in_knowledge61); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:69:30: (pages+= page )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==Page) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:69:30: pages+= page
                	    {
                	    pushFollow(FOLLOW_page_in_knowledge65);
                	    pages=page();

                	    state._fsp--;

                	    if (list_pages==null) list_pages=new ArrayList();
                	    list_pages.add(pages.getTemplate());


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }


            // TEMPLATE REWRITE
            // 69:39: -> foo(i=$pages)
            {
                retval.st = templateLib.getInstanceOf("foo",
              new STAttrMap().put("i", list_pages));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "knowledge"

    public static class page_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "page"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:70:1: page : ^( Page textbox picture (i+= shape )* ) -> root(it=$iqid=questionidpopq=$textbox.stpicname=picname);
    public final VisiotoXML.page_return page() throws RecognitionException {
        VisiotoXML.page_return retval = new VisiotoXML.page_return();
        retval.start = input.LT(1);

        List list_i=null;
        VisiotoXML.textbox_return textbox1 = null;

        VisiotoXML.shape_return i = null;
        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:70:6: ( ^( Page textbox picture (i+= shape )* ) -> root(it=$iqid=questionidpopq=$textbox.stpicname=picname))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:70:8: ^( Page textbox picture (i+= shape )* )
            {
            match(input,Page,FOLLOW_Page_in_page84); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_textbox_in_page86);
            textbox1=textbox();

            state._fsp--;

            pushFollow(FOLLOW_picture_in_page88);
            picture();

            state._fsp--;

            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:70:32: (i+= shape )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==Shape) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:70:32: i+= shape
            	    {
            	    pushFollow(FOLLOW_shape_in_page92);
            	    i=shape();

            	    state._fsp--;

            	    if (list_i==null) list_i=new ArrayList();
            	    list_i.add(i.getTemplate());


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 70:42: -> root(it=$iqid=questionidpopq=$textbox.stpicname=picname)
            {
                retval.st = templateLib.getInstanceOf("root",
              new STAttrMap().put("it", list_i).put("qid", questionid).put("popq", (textbox1!=null?textbox1.st:null)).put("picname", picname));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "page"

    public static class picture_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "picture"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:71:1: picture : ^( Picture x y width height ) ;
    public final VisiotoXML.picture_return picture() throws RecognitionException {
        VisiotoXML.picture_return retval = new VisiotoXML.picture_return();
        retval.start = input.LT(1);

        VisiotoXML.x_return x2 = null;

        VisiotoXML.y_return y3 = null;

        VisiotoXML.width_return width4 = null;

        VisiotoXML.height_return height5 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:71:9: ( ^( Picture x y width height ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:71:11: ^( Picture x y width height )
            {
            match(input,Picture,FOLLOW_Picture_in_picture126); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_x_in_picture128);
            x2=x();

            state._fsp--;

            pushFollow(FOLLOW_y_in_picture130);
            y3=y();

            state._fsp--;

            pushFollow(FOLLOW_width_in_picture132);
            width4=width();

            state._fsp--;

            pushFollow(FOLLOW_height_in_picture134);
            height5=height();

            state._fsp--;


            match(input, Token.UP, null); 
            setCorner((x2!=null?x2.d:0.0),(y3!=null?y3.d:0.0), (width4!=null?width4.d:0.0), (height5!=null?height5.d:0.0));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "picture"

    public static class textbox_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "textbox"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:72:1: textbox : ^( Box x y width height textboxtext ) -> string(w=$textboxtext.st);
    public final VisiotoXML.textbox_return textbox() throws RecognitionException {
        VisiotoXML.textbox_return retval = new VisiotoXML.textbox_return();
        retval.start = input.LT(1);

        VisiotoXML.textboxtext_return textboxtext6 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:72:8: ( ^( Box x y width height textboxtext ) -> string(w=$textboxtext.st))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:72:10: ^( Box x y width height textboxtext )
            {
            match(input,Box,FOLLOW_Box_in_textbox144); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_x_in_textbox146);
            x();

            state._fsp--;

            pushFollow(FOLLOW_y_in_textbox148);
            y();

            state._fsp--;

            pushFollow(FOLLOW_width_in_textbox150);
            width();

            state._fsp--;

            pushFollow(FOLLOW_height_in_textbox152);
            height();

            state._fsp--;

            pushFollow(FOLLOW_textboxtext_in_textbox154);
            textboxtext6=textboxtext();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 72:45: -> string(w=$textboxtext.st)
            {
                retval.st = templateLib.getInstanceOf("string",
              new STAttrMap().put("w", (textboxtext6!=null?textboxtext6.st:null)));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "textbox"

    public static class shape_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "shape"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:73:1: shape : ^( Shape x y width height shapetext ) -> shape(xstart=getXstart($x.d, $width.d)xend=getXend($x.d, $width.d)ystart=getYstart($y.d, $height.d)yend=getYend($y.d, $height.d)text=$shapetext.st);
    public final VisiotoXML.shape_return shape() throws RecognitionException {
        VisiotoXML.shape_return retval = new VisiotoXML.shape_return();
        retval.start = input.LT(1);

        VisiotoXML.x_return x7 = null;

        VisiotoXML.width_return width8 = null;

        VisiotoXML.y_return y9 = null;

        VisiotoXML.height_return height10 = null;

        VisiotoXML.shapetext_return shapetext11 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:73:7: ( ^( Shape x y width height shapetext ) -> shape(xstart=getXstart($x.d, $width.d)xend=getXend($x.d, $width.d)ystart=getYstart($y.d, $height.d)yend=getYend($y.d, $height.d)text=$shapetext.st))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:73:9: ^( Shape x y width height shapetext )
            {
            match(input,Shape,FOLLOW_Shape_in_shape171); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_x_in_shape173);
            x7=x();

            state._fsp--;

            pushFollow(FOLLOW_y_in_shape175);
            y9=y();

            state._fsp--;

            pushFollow(FOLLOW_width_in_shape177);
            width8=width();

            state._fsp--;

            pushFollow(FOLLOW_height_in_shape179);
            height10=height();

            state._fsp--;

            pushFollow(FOLLOW_shapetext_in_shape181);
            shapetext11=shapetext();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 73:45: -> shape(xstart=getXstart($x.d, $width.d)xend=getXend($x.d, $width.d)ystart=getYstart($y.d, $height.d)yend=getYend($y.d, $height.d)text=$shapetext.st)
            {
                retval.st = templateLib.getInstanceOf("shape",
              new STAttrMap().put("xstart", getXstart((x7!=null?x7.d:0.0), (width8!=null?width8.d:0.0))).put("xend", getXend((x7!=null?x7.d:0.0), (width8!=null?width8.d:0.0))).put("ystart", getYstart((y9!=null?y9.d:0.0), (height10!=null?height10.d:0.0))).put("yend", getYend((y9!=null?y9.d:0.0), (height10!=null?height10.d:0.0))).put("text", (shapetext11!=null?shapetext11.st:null)));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shape"

    public static class x_return extends TreeRuleReturnScope {
        public double d;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "x"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:74:1: x returns [double d] : ^( Xcoord mydouble ) ;
    public final VisiotoXML.x_return x() throws RecognitionException {
        VisiotoXML.x_return retval = new VisiotoXML.x_return();
        retval.start = input.LT(1);

        VisiotoXML.mydouble_return mydouble12 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:74:21: ( ^( Xcoord mydouble ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:74:23: ^( Xcoord mydouble )
            {
            match(input,Xcoord,FOLLOW_Xcoord_in_x222); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_mydouble_in_x224);
            mydouble12=mydouble();

            state._fsp--;


            match(input, Token.UP, null); 
            retval.d =Double.parseDouble((mydouble12!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(mydouble12.start),
              input.getTreeAdaptor().getTokenStopIndex(mydouble12.start))):null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "x"

    public static class y_return extends TreeRuleReturnScope {
        public double d;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "y"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:75:1: y returns [double d] : ^( Ycoord mydouble ) ;
    public final VisiotoXML.y_return y() throws RecognitionException {
        VisiotoXML.y_return retval = new VisiotoXML.y_return();
        retval.start = input.LT(1);

        VisiotoXML.mydouble_return mydouble13 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:75:21: ( ^( Ycoord mydouble ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:75:23: ^( Ycoord mydouble )
            {
            match(input,Ycoord,FOLLOW_Ycoord_in_y238); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_mydouble_in_y240);
            mydouble13=mydouble();

            state._fsp--;


            match(input, Token.UP, null); 
            retval.d =Double.parseDouble((mydouble13!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(mydouble13.start),
              input.getTreeAdaptor().getTokenStopIndex(mydouble13.start))):null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "y"

    public static class width_return extends TreeRuleReturnScope {
        public double d;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "width"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:76:1: width returns [double d] : ^( Width mydouble ) ;
    public final VisiotoXML.width_return width() throws RecognitionException {
        VisiotoXML.width_return retval = new VisiotoXML.width_return();
        retval.start = input.LT(1);

        VisiotoXML.mydouble_return mydouble14 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:76:25: ( ^( Width mydouble ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:76:27: ^( Width mydouble )
            {
            match(input,Width,FOLLOW_Width_in_width254); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_mydouble_in_width256);
            mydouble14=mydouble();

            state._fsp--;


            match(input, Token.UP, null); 
            retval.d =Double.parseDouble((mydouble14!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(mydouble14.start),
              input.getTreeAdaptor().getTokenStopIndex(mydouble14.start))):null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "width"

    public static class height_return extends TreeRuleReturnScope {
        public double d;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "height"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:77:1: height returns [double d] : ^( Height mydouble ) ;
    public final VisiotoXML.height_return height() throws RecognitionException {
        VisiotoXML.height_return retval = new VisiotoXML.height_return();
        retval.start = input.LT(1);

        VisiotoXML.mydouble_return mydouble15 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:77:26: ( ^( Height mydouble ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:77:28: ^( Height mydouble )
            {
            match(input,Height,FOLLOW_Height_in_height270); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_mydouble_in_height272);
            mydouble15=mydouble();

            state._fsp--;


            match(input, Token.UP, null); 
            retval.d =Double.parseDouble((mydouble15!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(mydouble15.start),
              input.getTreeAdaptor().getTokenStopIndex(mydouble15.start))):null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "height"

    public static class shapetext_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "shapetext"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:78:1: shapetext : ^( Shapetext text ) -> string(w=delXML($text.text));
    public final VisiotoXML.shapetext_return shapetext() throws RecognitionException {
        VisiotoXML.shapetext_return retval = new VisiotoXML.shapetext_return();
        retval.start = input.LT(1);

        VisiotoXML.text_return text16 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:78:10: ( ^( Shapetext text ) -> string(w=delXML($text.text)))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:78:12: ^( Shapetext text )
            {
            match(input,Shapetext,FOLLOW_Shapetext_in_shapetext282); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_text_in_shapetext284);
            text16=text();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 78:30: -> string(w=delXML($text.text))
            {
                retval.st = templateLib.getInstanceOf("string",
              new STAttrMap().put("w", delXML((text16!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(text16.start),
              input.getTreeAdaptor().getTokenStopIndex(text16.start))):null))));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shapetext"

    public static class textboxtext_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "textboxtext"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:79:1: textboxtext : ^( Textboxtext a= file o1= INT o2= INT questionid (pops+= popup )* ) -> foo(i=$pops);
    public final VisiotoXML.textboxtext_return textboxtext() throws RecognitionException {
        VisiotoXML.textboxtext_return retval = new VisiotoXML.textboxtext_return();
        retval.start = input.LT(1);

        CommonTree o1=null;
        CommonTree o2=null;
        List list_pops=null;
        VisiotoXML.file_return a = null;

        VisiotoXML.questionid_return questionid17 = null;

        VisiotoXML.popup_return pops = null;
        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:79:12: ( ^( Textboxtext a= file o1= INT o2= INT questionid (pops+= popup )* ) -> foo(i=$pops))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:79:14: ^( Textboxtext a= file o1= INT o2= INT questionid (pops+= popup )* )
            {
            match(input,Textboxtext,FOLLOW_Textboxtext_in_textboxtext301); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_file_in_textboxtext305);
            a=file();

            state._fsp--;

            o1=(CommonTree)match(input,INT,FOLLOW_INT_in_textboxtext309); 
            o2=(CommonTree)match(input,INT,FOLLOW_INT_in_textboxtext313); 
            pushFollow(FOLLOW_questionid_in_textboxtext315);
            questionid17=questionid();

            state._fsp--;

            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:79:64: (pops+= popup )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==Popup) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:79:64: pops+= popup
            	    {
            	    pushFollow(FOLLOW_popup_in_textboxtext319);
            	    pops=popup();

            	    state._fsp--;

            	    if (list_pops==null) list_pops=new ArrayList();
            	    list_pops.add(pops.getTemplate());


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input, Token.UP, null); 
            questionid = (questionid17!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(questionid17.start),
              input.getTreeAdaptor().getTokenStopIndex(questionid17.start))):null); ox = Integer.parseInt((o1!=null?o1.getText():null)); oy =Integer.parseInt((o2!=null?o2.getText():null)); picname = (a!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(a.start),
              input.getTreeAdaptor().getTokenStopIndex(a.start))):null);


            // TEMPLATE REWRITE
            // 79:190: -> foo(i=$pops)
            {
                retval.st = templateLib.getInstanceOf("foo",
              new STAttrMap().put("i", list_pops));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "textboxtext"

    public static class file_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "file"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:80:1: file : ^( Text name DOT name ) ;
    public final VisiotoXML.file_return file() throws RecognitionException {
        VisiotoXML.file_return retval = new VisiotoXML.file_return();
        retval.start = input.LT(1);

        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:80:6: ( ^( Text name DOT name ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:80:8: ^( Text name DOT name )
            {
            match(input,Text,FOLLOW_Text_in_file339); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_name_in_file341);
            name();

            state._fsp--;

            match(input,DOT,FOLLOW_DOT_in_file343); 
            pushFollow(FOLLOW_name_in_file345);
            name();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "file"

    public static class questionid_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "questionid"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:81:1: questionid : ^( QID name ) ;
    public final VisiotoXML.questionid_return questionid() throws RecognitionException {
        VisiotoXML.questionid_return retval = new VisiotoXML.questionid_return();
        retval.start = input.LT(1);

        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:81:12: ( ^( QID name ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:81:14: ^( QID name )
            {
            match(input,QID,FOLLOW_QID_in_questionid354); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_name_in_questionid356);
            name();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "questionid"

    public static class popup_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "popup"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:82:1: popup : ^( Popup a= text b= text ) -> popupquestion(target=$b.textanswer=$a.text);
    public final VisiotoXML.popup_return popup() throws RecognitionException {
        VisiotoXML.popup_return retval = new VisiotoXML.popup_return();
        retval.start = input.LT(1);

        VisiotoXML.text_return a = null;

        VisiotoXML.text_return b = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:82:7: ( ^( Popup a= text b= text ) -> popupquestion(target=$b.textanswer=$a.text))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:82:9: ^( Popup a= text b= text )
            {
            match(input,Popup,FOLLOW_Popup_in_popup365); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_text_in_popup369);
            a=text();

            state._fsp--;

            pushFollow(FOLLOW_text_in_popup373);
            b=text();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 82:32: -> popupquestion(target=$b.textanswer=$a.text)
            {
                retval.st = templateLib.getInstanceOf("popupquestion",
              new STAttrMap().put("target", (b!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(b.start),
              input.getTreeAdaptor().getTokenStopIndex(b.start))):null)).put("answer", (a!=null?(input.getTokenStream().toString(
              input.getTreeAdaptor().getTokenStartIndex(a.start),
              input.getTreeAdaptor().getTokenStopIndex(a.start))):null)));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "popup"

    public static class text_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "text"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:83:1: text : ^( Text name ) -> string(w=$name.value);
    public final VisiotoXML.text_return text() throws RecognitionException {
        VisiotoXML.text_return retval = new VisiotoXML.text_return();
        retval.start = input.LT(1);

        VisiotoXML.name_return name18 = null;


        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:83:6: ( ^( Text name ) -> string(w=$name.value))
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:83:8: ^( Text name )
            {
            match(input,Text,FOLLOW_Text_in_text396); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_name_in_text398);
            name18=name();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 83:21: -> string(w=$name.value)
            {
                retval.st = templateLib.getInstanceOf("string",
              new STAttrMap().put("w", (name18!=null?name18.value:null)));
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "text"

    public static class mydouble_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "mydouble"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:85:1: mydouble : ^( MyDouble ( MINUS )? INT DOT INT ) ;
    public final VisiotoXML.mydouble_return mydouble() throws RecognitionException {
        VisiotoXML.mydouble_return retval = new VisiotoXML.mydouble_return();
        retval.start = input.LT(1);

        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:85:10: ( ^( MyDouble ( MINUS )? INT DOT INT ) )
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:85:12: ^( MyDouble ( MINUS )? INT DOT INT )
            {
            match(input,MyDouble,FOLLOW_MyDouble_in_mydouble417); 

            match(input, Token.DOWN, null); 
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:85:23: ( MINUS )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==MINUS) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:85:23: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_mydouble419); 

                    }
                    break;

            }

            match(input,INT,FOLLOW_INT_in_mydouble422); 
            match(input,DOT,FOLLOW_DOT_in_mydouble424); 
            match(input,INT,FOLLOW_INT_in_mydouble426); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mydouble"

    public static class name_return extends TreeRuleReturnScope {
        public String value;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "name"
    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:86:1: name returns [String value] : ( ( String )* ( ID | INT ) ( ID | INT | String )* | String );
    public final VisiotoXML.name_return name() throws RecognitionException {
        VisiotoXML.name_return retval = new VisiotoXML.name_return();
        retval.start = input.LT(1);

        CommonTree String19=null;

        try {
            // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:87:1: ( ( String )* ( ID | INT ) ( ID | INT | String )* | String )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==String) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==UP||LA7_1==DOT) ) {
                    alt7=2;
                }
                else if ( ((LA7_1>=String && LA7_1<=INT)||LA7_1==ID) ) {
                    alt7=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA7_0==INT||LA7_0==ID) ) {
                alt7=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:87:3: ( String )* ( ID | INT ) ( ID | INT | String )*
                    {
                    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:87:3: ( String )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==String) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:87:3: String
                    	    {
                    	    match(input,String,FOLLOW_String_in_name438); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    if ( input.LA(1)==INT||input.LA(1)==ID ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:87:20: ( ID | INT | String )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>=String && LA6_0<=INT)||LA6_0==ID) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:
                    	    {
                    	    if ( (input.LA(1)>=String && input.LA(1)<=INT)||input.LA(1)==ID ) {
                    	        input.consume();
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    retval.value =input.getTokenStream().toString(
                      input.getTreeAdaptor().getTokenStartIndex(retval.start),
                      input.getTreeAdaptor().getTokenStopIndex(retval.start));

                    }
                    break;
                case 2 :
                    // D:\\eclipse Workspace\\d3web-KnOfficeParser\\Grammars\\VisiotoXML.g:88:3: String
                    {
                    String19=(CommonTree)match(input,String,FOLLOW_String_in_name460); 
                    retval.value =delQuotes((String19!=null?String19.getText():null));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "name"

    // Delegated rules


 

    public static final BitSet FOLLOW_Knowledge_in_knowledge61 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_page_in_knowledge65 = new BitSet(new long[]{0x0020000000000008L});
    public static final BitSet FOLLOW_Page_in_page84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_textbox_in_page86 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_picture_in_page88 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_shape_in_page92 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_Picture_in_picture126 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_x_in_picture128 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_y_in_picture130 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_width_in_picture132 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_height_in_picture134 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Box_in_textbox144 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_x_in_textbox146 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_y_in_textbox148 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_width_in_textbox150 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_height_in_textbox152 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_textboxtext_in_textbox154 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Shape_in_shape171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_x_in_shape173 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_y_in_shape175 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_width_in_shape177 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_height_in_shape179 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_shapetext_in_shape181 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Xcoord_in_x222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mydouble_in_x224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Ycoord_in_y238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mydouble_in_y240 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Width_in_width254 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mydouble_in_width256 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Height_in_height270 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_mydouble_in_height272 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Shapetext_in_shapetext282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_shapetext284 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Textboxtext_in_textboxtext301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_file_in_textboxtext305 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_textboxtext309 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_textboxtext313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_questionid_in_textboxtext315 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000004L});
    public static final BitSet FOLLOW_popup_in_textboxtext319 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000004L});
    public static final BitSet FOLLOW_Text_in_file339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_name_in_file341 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_DOT_in_file343 = new BitSet(new long[]{0x0010000000000030L});
    public static final BitSet FOLLOW_name_in_file345 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QID_in_questionid354 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_name_in_questionid356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Popup_in_popup365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_popup369 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_text_in_popup373 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Text_in_text396 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_name_in_text398 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MyDouble_in_mydouble417 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_MINUS_in_mydouble419 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_mydouble422 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_DOT_in_mydouble424 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_INT_in_mydouble426 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_String_in_name438 = new BitSet(new long[]{0x0010000000000030L});
    public static final BitSet FOLLOW_set_in_name441 = new BitSet(new long[]{0x0010000000000032L});
    public static final BitSet FOLLOW_set_in_name447 = new BitSet(new long[]{0x0010000000000032L});
    public static final BitSet FOLLOW_String_in_name460 = new BitSet(new long[]{0x0000000000000002L});

}