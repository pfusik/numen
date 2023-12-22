#include <stdlib.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>

int reverse_transparency = 1;

#define error(z) { printf("Error: %s !\n",z); exit(1); } 
#define xread(fd,buf,cnt) { if ((cnt) != read(fd,buf,cnt)) error("Can't read further"); }

#define Gfx_SOLID 1
#define Gfx_SPRITE 2
#define Gfx_SKY 3
#define Gfx_WEAPON 4
int Gfx_type[256];
//int Gfx_data[256];
int Gfx_tile[256];
int count[5] = {0, 0, 0, 0, 0};
int before;

void readGfxScript(char *name)
{
	char *Gfx_TYPE[5] = {"none", "solid", "sprite", "sky", "weapon"};
	char line[300];
	FILE *fp = fopen(name, "rt");
	if (fp == NULL)
		error("can't open gfx script file");
	memset(Gfx_type, 0, sizeof(Gfx_type));
	printf("\n; Gfx scipt ----------------\n");
	while (5) {
		int tile;
		char *p;
		int type;
		fgets(line, 300, fp);
		if (feof(fp))
			break;
		if (line[0] == '#' || line[0] == '\n')
			continue;
		sscanf(line, "%d", &tile);
		p = line;
		while (*p == ' ' || *p == '\t' || (*p >= '0' && *p <= '9'))
			p++;
		if (strncmp(p, "solid", 5) == 0)
			type = Gfx_SOLID;
		else if (strncmp(p, "sprite", 6) == 0)
			type = Gfx_SPRITE;
		else if (strncmp(p, "sky", 3) == 0)
			type = Gfx_SKY;
		else if (strncmp(p, "weapon", 6) == 0)
			type = Gfx_WEAPON;
		else
			error("wrong tile type in gfx script file");
		Gfx_type[tile] = type;
		//Gfx_data[tile] = count[type]++;
		count[type]++;
		//printf("; tile %d: %s #%d\n", tile, Gfx_TYPE[type], Gfx_data[tile]);
		printf("; tile %d: %s\n", tile, Gfx_TYPE[type]);
	}
	fclose(fp);
}

struct col
{
	int moved;
	int destIndex;
	int destOffset;
	int y;
	unsigned char *data ;
	int offset;
};

struct tile
{
	int x;
	int y;
	int start;
	int type;
};

int tilestart,tileend, tilecnt;
short *tilesizx, *tilesizy;
int *picanm;
struct tile *tiles;
struct col cols[16384];
int colcnt;

unsigned char data[1024 * 1024];
unsigned char temp[128 * 128];

void readTiles(char *fname) 
{
	int buf[4];
	int tile;
	int column;
	unsigned char *src, *dest;
	int tilcnt;
	int tilecolumns;
	int columnlength;
	int x, y;
	
	int fd = open(fname, O_RDONLY
#ifdef O_BINARY
		| O_BINARY
#endif
		);
	xread(fd, buf, 16);
	printf("\n; Art file -----------------\n");
	printf("; Artversion: %d\n", buf[0]);
	if (buf[0] != 1 )
		error("Bad artversion");
	printf("; Numtiles:   %d\n", buf[1]);
	printf("; Tilestart:  %d\n", buf[2]);
	tilestart=buf[2];
	printf("; Tileend:    %d\n", buf[3]);
	tileend=buf[3];
	tilecnt=tileend-tilestart+1;
	printf("; Tilecnt:    %d\n", tilecnt);

	tilesizx=malloc(tilecnt*sizeof(short));
	tilesizy=malloc(tilecnt*sizeof(short));
	picanm=malloc(tilecnt*sizeof(int));
	
	xread(fd, tilesizx, tilecnt*sizeof(short));
	xread(fd, tilesizy, tilecnt*sizeof(short));
	xread(fd, picanm, tilecnt*sizeof(int));

	read(fd,data,sizeof(data));
	tiles = malloc(tilecnt*sizeof(struct tile));
	colcnt = 0;
	tilcnt = 0;
	src = data;
	dest = data;
	for (tile = 0; tile < tilecnt; tile++) {
		switch (Gfx_type[tile]) {
		case Gfx_SPRITE:
			Gfx_tile[tile] = tilcnt;
			tiles[tilcnt].x = tilecolumns = tilesizx[tile];
			tiles[tilcnt].y = columnlength = tilesizy[tile];
			tiles[tilcnt].start = colcnt;
			tiles[tilcnt].type = Gfx_SPRITE;
			tilcnt++;
			for (column = 0; column < tilecolumns; column++) {
				cols[colcnt].y = columnlength;
				cols[colcnt].data = dest;
				cols[colcnt].moved = 0;
				cols[colcnt].destIndex = colcnt;
				cols[colcnt].destOffset = 0;
				colcnt++;
				for (x = 0; x < columnlength; x++) {
					if (reverse_transparency)
						*dest = *src == 255 ? 0 : 255 - ((*src & 15) | ((*src & 15) << 4));
					else
						*dest = *src == 255 ? 0 : *src == 0 ? 0x11 : (*src & 15) | ((*src & 15) << 4);
					src++;
					dest++;
				}
			}
			break;
		case Gfx_SKY:
		case Gfx_WEAPON:
			Gfx_tile[tile] = tilcnt;
			tiles[tilcnt].x = tilecolumns = tilesizy[tile];
			if (tilesizx[tile] & 1)
				error("sky/weapon tile width is odd");
			tiles[tilcnt].y = columnlength = tilesizx[tile] / 2;
			tiles[tilcnt].start = colcnt;
			tiles[tilcnt].type = Gfx_type[tile];
			tilcnt++;

			memcpy(temp, src, tilesizx[tile] * tilesizy[tile]);
			src += tilesizx[tile] * tilesizy[tile];

			for (y = 0; y < tilecolumns; y++) {
				cols[colcnt].y = columnlength;
				cols[colcnt].data = dest;
				cols[colcnt].moved = 0;
				cols[colcnt].destIndex = colcnt;
				cols[colcnt].destOffset = 0;
				colcnt++;
				for (x = 0; x < tilesizx[tile]; x += 2) {
					unsigned char hi = temp[x * tilecolumns + y];
					unsigned char lo = temp[(x + 1) * tilecolumns + y];
					if (reverse_transparency && Gfx_type[tile] == Gfx_WEAPON) {
						if (hi != 255)
							hi = 15 - (hi & 15);
						if (lo != 255)
							lo = 15 - (lo & 15);
					}
					if (hi == 255)
						hi = 0;
					if (lo == 255)
						lo = 0;
					*dest++ = ((hi & 15) << 4) | (lo & 15);
				}
			}
			break;
		default:
			src += tilesizx[tile] * tilesizy[tile];
			break;
		}
	}

	tilecnt = tilcnt;
	before = dest - data;
}

/* gives position of cols[a] in cols[b], otherwise -1 */
int subcol(int a, int b)
{
	int i;
	struct col  ca=cols[a], cb=cols[b];	
	if (ca.y>cb.y)
		return -1;
	for(i=0;i<=(cb.y-ca.y);i++)
	{
		if (!memcmp(ca.data, cb.data+i, ca.y))
			return i;
	}
	return -1;
}

/* return length of maximum a's tail being b's head */
int tlhd( int a, int b)
{
	int i;
	struct col ca=cols[a], cb=cols[b];
	int start=ca.y-cb.y;
	if (start<0) start=0;
	for(i=start;i<ca.y;i++)
	{
		int len=ca.y-i;
		if (!memcmp(ca.data+i,cb.data,len))
			return len;
	}
	return 0;
}

void moveCols( int dst, int src, int off)
{
	int i;
	for (i=0;i<colcnt;i++)
	{
		if (cols[i].destIndex==src)
		{
			cols[i].moved=1;
			cols[i].destIndex=dst;
			cols[i].destOffset+=off;
		}
	}
}

void optimize() 
{
	int opts=1,i,j,pos,len;
	while(opts)
	{
		fprintf(stderr,"Starting optimising\n");
		opts=0;

		// 1. znajdz cols bedace podcolumnami innych 
		for (i=0;i<colcnt;i++)
		{
			if (cols[i].moved)
				continue;
			for(j=0;j<colcnt;j++)
			{
				if (i==j || cols[j].moved)
					continue;	
				if (0<=(pos=subcol(i,j))) //i is a subcol of j
				{
					moveCols(j,i,pos); //copy all cols from i to j, add pos
					opts++;					
					break ;
				}
			}
		}
		fprintf(stderr, "  After 1: Made %d optimisations\n",opts);
//		break; //only 1st step
		// 2. znajdz i,j takie ze tl(i)=hd(j) 
		for(i=0;i<colcnt;i++)
		{
			int best=-1,max=0;
			if (cols[i].moved)
				continue;
			for(j=0;j<colcnt;j++)
			{
				if (i==j || cols[j].moved)
					continue;
				if ((len=tlhd(i,j))>max)
				{
					max=len;
					best=j;
				}
			}
			if (best>=0)
			{//found such a tail/head combination
				int oldy=cols[i].y;
				int newy=oldy+cols[best].y-max;
				char *oldi=cols[i].data;
//				printf("Let's move %d to tail of %d, len=%d\n", best,i,max);
//				printf(" cols[best].moved=%d\n",cols[best].moved);
				cols[i].data=malloc(newy);
				memcpy(cols[i].data, oldi, cols[i].y);
				memcpy(cols[i].data+oldy, cols[best].data+max, cols[best].y-max);
				//free(oldi);	FIXME!!!!!!!!!!!
				moveCols(i,best,oldy-max);
				cols[i].y=newy;
				opts++;
//				printf("    moveCols(%d,%d,%d)\n",i,best,oldy-max);
			}
//			if (opts) break ; //X-MAN
		}
		fprintf(stderr, "  After 2: Made %d optimisations\n",opts);
//		opts=0; //X-MAN
	}
}

void saveDat(unsigned char *data, int size, int perrow)
{
	int i,ii;
	for(i=0;i<size;i++)
	{
		ii=i%perrow;
		if (!ii)
			printf("\tdta\t");
		printf("$%02x",data[i]);
		if (ii<(perrow-1) && i<size-1)
			printf(",");
		else
			printf("\n");
	}
}

void saveASX() 
{
	int i,j,n,offset=0;
	printf("\n; graphics data\n");
	printf("\torg\tPicture_columns\n");
	for (i=0;i<colcnt;i++)
	{
		if (cols[i].moved)
			continue;
		cols[i].offset = offset;
		saveDat(cols[i].data, cols[i].y, 16);
		offset += cols[i].y;
		printf("\n");
	}
	printf("; before: %d\n", before);
	printf("; after: %d\n", offset);
	if (before > 0)
		printf("; gain: %d%%\n", 100 * (before - offset) / before);

	for(i=0,n=0;i<tilecnt;i++)
		if (tiles[i].type == Gfx_SPRITE) {
			int X=tiles[i].x;
			int start=tiles[i].start;
			printf("Picture_%03dColumns\n",n);
			for(j=0;j<X;j++)
			{
				int jj=j%4;
				if (!jj)
					printf("\tdta\ta(");
				printf("Picture_columns+$%04x",cols[cols[start+j].destIndex].offset
					+cols[start+j].destOffset);
				if (jj<3 && j<X-1)
					printf(",");
				else
					printf(")\n");
			}
			n++;
		}

/* sky */
	for (i = 0; i < tilecnt; i++)
		if (tiles[i].type == Gfx_SKY) {
			int X=tiles[i].x;
			int start=tiles[i].start;
			printf("\n; sky picture\n");
			//printf("Picture_SKY_WIDTH\tequ\t%d\n", 2 * tiles[i].y);
			printf("\tert\tPicture_SKY_WIDTH!=%d\n", 2 * tiles[i].y);
			printf("\torg\tPicture_skyColumn_lo\n");
			for(j=0;j<X;j++)
			{
				int jj=j%4;
				if (!jj)
					printf("\tdta\tl(");
				printf("Picture_columns+$%04x",cols[cols[start+j].destIndex].offset
					+cols[start+j].destOffset);
				if (jj<3 && j<X-1)
					printf(",");
				else
					printf(")\n");
			}
			printf("\torg\tPicture_skyColumn_hi\n");
			for(j=0;j<X;j++)
			{
				int jj=j%4;
				if (!jj)
					printf("\tdta\th(");
				printf("Picture_columns+$%04x",cols[cols[start+j].destIndex].offset
					+cols[start+j].destOffset);
				if (jj<3 && j<X-1)
					printf(",");
				else
					printf(")\n");
			}

		}

/* sprites */
	printf("\n; sprite pictures\n");
	printf("\torg\tPicture_column_lo\n");
	for (j = 0, i = 0; i < tilecnt; i++)
		if (tiles[i].type == Gfx_SPRITE) {
			if (j % 4 == 0)
				printf("\tdta\tl(");
			printf("Picture_%03dColumns", j);
			if (j % 4 == 3 || j == count[Gfx_SPRITE] - 1)
				printf(")\n");
			else
				printf(",");
			j++;
		}

	printf("\torg\tPicture_column_hi\n");
	for (j = 0, i = 0; i < tilecnt; i++)
		if (tiles[i].type == Gfx_SPRITE) {
			if (j % 4 == 0)
				printf("\tdta\th(");
			printf("Picture_%03dColumns", i);
			if (j % 4 == 3 || j == count[Gfx_SPRITE] - 1)
				printf(")\n");
			else
				printf(",");
			j++;
		}
	printf("\torg\tPicture_width\n");
	for (j = 0, i = 0; i < tilecnt; i++)
		if (tiles[i].type == Gfx_SPRITE) {
			if (j % 4 == 0)
				printf("\tdta\t");
			printf("%d", tiles[i].x);
			if (j % 4 == 3 || j == count[Gfx_SPRITE] - 1)
				printf("\n");
			else
				printf(",");
			j++;
		}

	printf("\torg\tPicture_height\n");
	for (j = 0, i = 0; i < tilecnt; i++)
		if (tiles[i].type == Gfx_SPRITE) {
			if (j % 4 == 0)
				printf("\tdta\t");
			printf("%d", tiles[i].y);
			if (j % 4 == 3 || j == count[Gfx_SPRITE] - 1)
				printf("\n");
			else
				printf(",");
			j++;
		}

/* guns */
	if (count[Gfx_WEAPON] > 0) {
		printf("\n; gun pictures\n");
		printf("Gun_lineAddress\n");
		for (j = 0, i = 0; i < tilecnt; i++)
			if (tiles[i].type == Gfx_WEAPON) {
				int X=tiles[i].x;
				int start=tiles[i].start;
				for(j=0;j<X;j++)
				{
					int jj=j%4;
					if (!jj)
						printf("\tdta\ta(");
					printf("Picture_columns+$%04x",cols[cols[start+j].destIndex].offset
						+cols[start+j].destOffset);
					if (jj<3 && j<X-1)
						printf(",");
					else
						printf(")\n");
				}
				n++;
			}
	
		printf("Gun_lineOffset\n\tdta\t0,");
		n = 0;
		for (j = 1, i = 0; i < tilecnt; i++)
			if (tiles[i].type == Gfx_WEAPON) {
				if (j % 4 == 0)
					printf("\tdta\t");
				printf("%d", n);
				if (j % 4 == 3 || j == count[Gfx_WEAPON])
					printf("\n");
				else
					printf(",");
				j++;
				n += 2 * tiles[i].x;
			}
	
		printf("Gun_column\n\tdta\t0,");
		for (j = 1, i = 0; i < tilecnt; i++)
			if (tiles[i].type == Gfx_WEAPON) {
				if (j % 4 == 0)
					printf("\tdta\t");
				printf("%d", (40-tiles[i].y)/2);
				if (j % 4 == 3 || j == count[Gfx_WEAPON])
					printf("\n");
				else
					printf(",");
				j++;
			}
	
		printf("Gun_width\n\tdta\t0,");
		for (j = 1, i = 0; i < tilecnt; i++)
			if (tiles[i].type == Gfx_WEAPON) {
				if (j % 4 == 0)
					printf("\tdta\t");
				printf("%d/2-1", 2*tiles[i].y);
				if (j % 4 == 3 || j == count[Gfx_WEAPON])
					printf("\n");
				else
					printf(",");
				j++;
			}
	
		printf("Gun_height\n\tdta\t0,");
		for (j = 1, i = 0; i < tilecnt; i++)
			if (tiles[i].type == Gfx_WEAPON) {
				if (j % 4 == 0)
					printf("\tdta\t");
				printf("%d", tiles[i].x);
				if (j % 4 == 3 || j == count[Gfx_WEAPON])
					printf("\n");
				else
					printf(",");
				j++;
			}
	}
}

int main(int argc, char **argv)
{
	if (argc!=3)
		error("Usage: ConvArt file.art gfx.txt");
	printf("* Generated automatically by ConvArt\n"
		   "* from %s and %s\n", argv[1], argv[2]);
	readGfxScript(argv[2]);
	readTiles(argv[1]);
	optimize();
	saveASX();

	return 0;
}
