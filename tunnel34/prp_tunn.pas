program sucker;

uses graph,crt;

var RTab,ATab:array[0..86,0..100] of byte;

procedure SetRGB(c,r,g,b:integer);
begin
port[$3c8]:=c;
 port[$3c9]:=r;
 port[$3c9]:=g;
 port[$3c9]:=b;
end; {SetRGB}

procedure Okienka;
var x:integer;
begin
SetColor(255);
Rectangle(20,50,21+101,51+87);
Line(20+51,51,20+51,51+87);
Line(20,50+44,21+101,50+44);
Rectangle(170,50,171+101,51+87);
for x:=0 to 255 do
 begin
    SetColor(x);
    Line(x,180,x,190);
 end ;

end;

procedure Funky;
var r,a,x,y:integer;
    i,c0,c1:byte;
    f:file;
    xx,rr:real;
    s:string;
	c,d,e,g:real;
    _S,_Z,_R,_T:real;
    min_R,max_R:real;
    min_T,max_T:real;
    _K,_A,_B:real;
    _QPA:real;
begin
for r:=0 to 150 do
  begin
   c0:=round(79{63}*(sin(pi*r/300))); (**)
(*	c0:=round(79*(cos(pi*r/300)/(sin(pi*r/300)+0.01))); (**)
{   c0:=round(39-39*(sin(pi*r/200)));}
(* 	c0:=round(79*(r-150/(r+1))/400); (**)
    rr:=r*67/150;
    for a:=0 to 359 do
      begin
        x:=round(50+rr*cos(a*pi/180)); {0-100}
        y:=round(43+rr*sin(a*pi/180)); {0-86}
        c1:=round(79{63}*a/360{+r/17});
        if c1>79 then c1:=c1-79;
        PutPixel(x+21,c0 div 4,255);
        PutPixel(x+21,y+51,c0+128);
        PutPixel(x+171,y+51,c1+128);
        if (x>=0) and (x<=100) and (y>=0) and (y<=86) then
          begin
            RTab[y,x]:=c0;
            ATab[y,x]:=c1;
          end;
      end;
  end;
min_R:=100000;
max_R:=0;
min_T:=100000;
max_T:=0;


_QPA := 3;
_K := 0.35;
_A := (1/_K)*80 / (1/_K-1) - 1;
_B := _A-79;
for x:=-50 to 50 do
 begin
    for y:=-43 to 43 do
     begin
        _R := sqrt(x*x + y*y);
        if (_R > max_R) then max_R:=_R;
        if (_R < min_R) then min_R:=_R;
        if (_R<=_QPA) then
            _T := 0
        else
         begin
            _R := (_R-_QPA);
            if (_R < 0) then _R := 0; {R=1..61}
            _R := _R/(66-_QPA) ; {R=0..1}
            _R := _R*(1-_K)+_K; {R=_K..1}
            _T := _A-_B/_R;
            if (_T<1) then _T:=1;
 	        if (_T > max_T) then max_T:=_T;
    	    if (_T < min_T) then min_T:=_T;
         end ;
        _Z := 128+_T;
        PutPixel(x+21+50,y+51+43,round(_Z)); {}
        RTab[y+43,x+50]:=round(_T); {}
     end
 end ;
  SetColor(191);
  str(min_R:4:2,s);
  OutTextXY(250,30,s);
  str(max_R:4:2,s);
  OutTextXY(250,40,s);
  str(min_T:4:2,s);
  OutTextXY(250,60,s);
  str(max_T:4:2,s);
  OutTextXY(250,70,s);

  Okienka;
  SetColor(191);
  str(c0,s);
  OutTextXY(0,30,s);
  str(c1,s);
  OutTextXY(0,40,s);

(*
c:=700;d:=10;e:=1;
for x:=0 to 100 do
	for y:=0 to 86 do
  	begin
      xx:=Sqrt(Sqr(x-50)+Sqr(y-43));
      if xx=0 then xx:=1e-5;
    	c0:=round( c/xx*e - d );
{      if c0>79 then c0:=79;}
      if c0>79 then c0:=(c0 and 15) +64;
      c0:=79-c0;
    	PutPixel(x+21,c0 div 4,255);
      PutPixel(x+21,y+51,c0+128);
    	RTab[y,x]:=c0;
    end;
{repeat until false;}
*)

  Assign(f,'tun_tabs.dat');
  Rewrite(f,1);
{  for a:=0 to 86 do
    begin RTab[a,0]:=0;RTab[a,100]:=0;
          ATab[a,0]:=0;ATab[a,100]:=0; end;
  for a:=0 to 100 do
    begin RTab[0,a]:=0;RTab[86,a]:=0;
          ATab[0,a]:=0;ATab[86,a]:=0;  end;}

  BlockWrite(f,RTab,101*87);
  BlockWrite(f,ATab,101*87);
  Close(f);
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
for i:=0 to 79 do SetRGB(i+128,i*4,i*4,i*2);
SetRGB(255,63,0,0);
Okienka;
Funky;
readkey;
CloseGraph;
end.
