include ../common.mak

.PHONY: run
run: tiplazma.obx
	$(RUNOBX) $<

tiplazma.nex tiplazma.obx: tiplazma.agf tiplazma.ray tiplazma.g10

tiplazma.agf tiplazma.ray tiplazma.g10: flower_f.pcx MakeTabs.class
	$(JAVA) MakeTabs $<

.PHONY: clean
clean:
	$(RM) tiplazma.nex tiplazma.obx
	$(RM) MakeTabs.class
	$(RM) tiplazma.agf tiplazma.ray tiplazma.g10
