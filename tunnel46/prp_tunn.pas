program sucker;

uses graph,crt;
const
	HSIZ = 64 ;
    VSIZ = 60 ;
    RCIR = 50 ;
    ACIR = 128 ;

var RTab,ATab:array[0..VSIZ-1,0..HSIZ-1] of byte;
    XRTab,XATab:array[-40..VSIZ+40,-30..HSIZ+30] of byte ;
	CTab:array[0..RCIR-1,0..ACIR-1] of byte ;

procedure SetRGB(c,r,g,b:integer);
begin
{$R-}
 port[$3c8]:=c;
 port[$3c9]:=r;
 port[$3c9]:=g;
 port[$3c9]:=b;
{$R+}
end; {SetRGB}

procedure Okienka;
begin
SetColor(255);
Rectangle(20,30,21+HSIZ,31+VSIZ);
Line(20+(HSIZ div 2),31,20+(HSIZ div 2),31+VSIZ);
Line(20,30+(VSIZ div 2),21+HSIZ,30+(VSIZ div 2));
Rectangle(120,30,121+HSIZ,31+VSIZ);
Rectangle(220,30,221+HSIZ,31+VSIZ);
Rectangle(20,120,21+HSIZ,121+VSIZ);
Line(20+(HSIZ div 2),121,20+(HSIZ div 2),121+VSIZ);
Line(20,120+(VSIZ div 2),21+HSIZ,120+(VSIZ div 2));
Rectangle(120,120,121+HSIZ,121+VSIZ);
end;

procedure Funky;
var r,a,x,y:integer;
    xc,yc:real;
    i,c0,c1:byte;
    f:file;
    xx,rr:real;
    re : real;
    s:string;
begin

for r:=0 to 300 do
  begin
(*	xc := 55 - 30*sin(pi*r/360) ; (**)
	xc := 50 - 30*sin(pi*r/360) ; (**)
	yc := 28 ;
    c0:=round(79{63}*(sin(pi*r/690))) mod 16; (**)
    rr:=r*51/300;
    for a:=0 to 359 do
      begin
        x:=round(xc+rr*cos(pi*a/180)); {0-100}
        y:=round(yc+rr*sin(pi*a/180)); {0-86}
        c1:=round(79*a/360{+r/17}) mod 16;
        if c1>79 then c1:=c1-79;
{       PutPixel(x+21,c0 div 4,255); {}
        PutPixel(x+21,y+31,c0+64);
        PutPixel(x+221,y+31,c1+64) ; {}
        PutPixel(x+121,y+31,(c0 div 8) * 4 + c1 mod 8 + 64) ;
        XRTab[y,x] := c0 ;
        XATab[y,x] := c1 ;
{        if (x>=0) and (x<=HSIZ-1) and (y>=0) and (y<=VSIZ-1) then
          begin
            RTab[y,x]:=c0;
            ATab[y,x]:=c1;
          end;
}
      end;
  end;
  Okienka;
  SetColor(191);
  str(c0,s);
  OutTextXY(0,30,s);
  str(c1,s);
  OutTextXY(0,40,s);


for r:=0 to 299 do
  begin
	xc := 32 ;
	yc := 30 ; {}
    re := r ;
    re := 50*re/300 ;
    if re > 49 then re := 49 ;
    c0:=round(re); {}
    rr:=r*44/300;
    for a:=0 to 359 do
      begin
        x:=Round(xc+rr*cos(a*pi/180));
        y:=Round(yc+rr*sin(a*pi/180));
        re := a ; {}
        re := re*127/359 ;
        c1:=round(re); {}
        if c1>127 then c1:=c1-127; {}
        PutPixel(x+21,y+121,64-c0+64);
        PutPixel(x+121,y+121,c1+64);
        CTab[c0,c1] := 16*XRTab[y,x]+XATab[y,x] ; {}
{        PutPixel(240+c0,70+c1,CTab[c0,c1]); {}
        if (x>=0) and (x<=HSIZ-1) and (y>=0) and (y<=VSIZ-1) then
          begin
            RTab[y,x]:=c0;
            ATab[y,x]:=c1;
          end;
      end;
  end;

  Okienka ;
  Assign(f,'TabR.dat'); Rewrite(f,1);
  BlockWrite(f,RTab,HSIZ*VSIZ); Close(f);
  Assign(f,'TabA.dat'); Rewrite(f,1);
  BlockWrite(f,ATab,HSIZ*VSIZ); Close(f);
  Assign(f,'TabC.dat'); Rewrite(f,1);
  BlockWrite(f,CTab,RCIR*ACIR); Close(f);

end;

var ster,tryb:integer;
    i:integer;

begin
ster:=installuserdriver('svga256',nil);
tryb:=0; {320x200}
InitGraph(ster,tryb, ''{'c:\!tp\bgi'});
if graphresult<>grok then
	begin
		Writeln('Blad inicjalizacji karty graficznej!!!');
		halt;
	end;
for i:=0 to 255 do SetRGB(i,i*4,i*4,i*4);
SetRGB(255,63,0,0);
Okienka;
Funky;
readkey;
CloseGraph;
end.
