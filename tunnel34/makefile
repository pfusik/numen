include ../common.mak

.PHONY: run
run: tunnel34.obx
	$(RUNOBX) $<

tunnel34.nex tunnel34.obx: bmap61.dat
tunnel34.obx: tun_tabs.dat

.PHONY: clean
clean:
	$(RM) tunnel34.nex tunnel34.obx
