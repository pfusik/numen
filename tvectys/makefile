include ../common.mak

.PHONY: run
run: tvectys.obx
	$(RUNOBX) $<

tvectys.obx: tvcalc.obx tvdisp.obx
	cat tvcalc.obx tvdisp.obx >tvectys.obx

.PHONY: clean
clean:
	$(RM) tvectys.obx tvcalc.obx tvdisp.nex tvdisp.obx
