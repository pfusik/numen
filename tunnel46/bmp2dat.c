#include <stdio.h>
#include <string.h>
#include <unistd.h>

int main()
{
    char pic[5000],dst[1024];  /* 1024=(8*15+8)*8  */
    int l,y,x;

    memset(dst,0,1024);
    memset(pic,0,5000);
    read(0,pic,3000);

    for(l=0;l<8;l++)
     	for(y=0;y<15;y++)
        	for(x=0;x<8;x++) {
            	int s=1078+(7-l)*240+(15-y)*16+x*2;
            	dst[l*128+y*8+x]=(pic[s] & 0xf0) | ((pic[s+1] & 0xf0) >> 4);
            }
	write(1,dst,1024);
    return 0;
}
