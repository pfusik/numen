include ../common.mak

.PHONY: run
run: tunnel46.obx
	$(RUNOBX) $<

tunnel46.nex tunnel46.obx: tabr.dat taba.dat tabc.dat tqa.dat
tunnel46.nex tunnel46.obx: bmap4.dat bmap7.dat bmap8.dat bmap2.dat

tqa.dat: bmp2dat.exe tqa.bmp
	./bmp2dat.exe <tqa.bmp >tqa.dat

.PHONY: clean
clean:
	$(RM) tunnel46.nex tunnel46.obx tqa.dat bmp2dat.exe
