program sucker;

uses graph,crt;
const
	HSIZ = 80 ;
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
    c,i,c0,c1:word;
    f:file;
    xx,rr:real;
    re : real;
    s:string;

    HorAngle,VertAngle       : LongInt ;
    CX, CY: Real;
    _X,_Y,_Z : integer ;
    __X,__Y,__Z : extended ;
    _XX,_ZZ : integer ;
	CONST H_H = 650;
    	  V_V = 500;
		  H_RAY = 60 ;
          V_RAY = 45 ;
          H_PI = 2*PI/H_H ;
          V_PI = 2*PI/V_V ;
begin

  for HorAngle:=0 To H_H div 2-1 do
   begin
    CX := Cos(2*pi*HorAngle/H_H)*H_RAY;
    CY := Sin(2*pi*HorAngle/H_H)*H_RAY ; {}
{    CY := 0 ; {}
    for VertAngle:=0 To V_V-1 Do Begin
      __X:=CX+Cos(VertAngle*V_PI)*Cos(HorAngle*H_PI)*V_RAY;
      __Y:=CY+Cos(VertAngle*V_PI)*Sin(HorAngle*H_PI)*V_RAY;
      __Z:=Sin(VertAngle*V_PI)*V_RAY;
      if ( VertAngle>V_V * 0.25 ) and
			(VertAngle<V_V * 0.75 ) then
        begin
	      _X := Trunc( __X * (300/(300-__Y))) ;
          _Z := Trunc( __Z * (300/(300-__Y))) ;
        end
      else
        begin
	      _X := Trunc( __X * (300/(300+__Y))) ;
          _Z := Trunc( __Z * (300/(300+__Y))) ;
        end;
{     _X := Trunc( __X ) ;
	  _Z := Trunc( __Z ) ; {}
      _Y := Trunc( __Y+64 ) ;
      if _Y < 0 then _Y := 0 ;
      if _Y > 255 then _Y := 255 ;
      c := Round((VertAngle/V_V*256)+(HorAngle/H_H*256)) mod 64;
      if ( GetPixel( _X+150, _Z+70 ) = 0 ) or ( GetPixel ( _X+150, _Z+70 ) > _Y ) then
         begin
	     PutPixel(_X+150, _Z+70 , _Y );
{         PutPixel(_X div 2 +170, _Z div 2+170, c ); }
		 _XX := _X + HSIZ div 2 ;
          _ZZ := _Z + VSIZ div 2 ;
          if ( _XX <= HSIZ + 30 ) and ( _XX >= -30 )
             and ( _ZZ <= VSIZ + 40 ) and ( _ZZ >= -40 )
             then
               begin
			     if ( VertAngle>V_V * 0.25 ) and
				 	( VertAngle<V_V * 0.75 ) then
				   begin
                     XRTAB[ _ZZ, _XX ] := 15 - Round(128*HorAngle/H_H) mod 16 ;
	                 XATab[ _ZZ, _XX ] := Round(256*VertAngle/V_V) mod 16;
				   end
                 else
				   begin                        {512,256}
				     XRTab[ _ZZ, _XX ] := Round(512*HorAngle/H_H) mod 16;
				     XATab[ _ZZ, _XX ] := Round(256*VertAngle/V_V) mod 16;
	               end ;
                 PutPixel(_X +100, _Z div 2+170, XRTab[ _ZZ,_XX] );
                 PutPixel(_X div 2 +240, _Z div 2+170, XATab[ _ZZ,_XX] );
               end ;
         end ;
    end;
  end;
readln ;
for r:=0 to 299 do
  begin
	xc := 40 ;
	yc := 30 ; {}
    re := r ;
    re := RCIR*re/300 ;
    if re > RCIR-1 then re := RCIR-1 ;
    c0:=Round(re); {}
    rr:=r*RCIR/300;
    for a:=0 to 359 do
      begin
        x:=Trunc(xc+rr*cos(a*pi/180));
        y:=Trunc(yc+rr*sin(a*pi/180));
        re := a ; {}
        re := re*(ACIR-1)/359 ;
        c1:=round(re) ; {}
        if c1>=ACIR then c1:=c1-ACIR; {}
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
Funky;
readkey;
CloseGraph;
end.
