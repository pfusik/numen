include ../common.mak

.PHONY: run
run: tunnel56.obx
	$(RUNOBX) $<

.PHONY: java
java: Test.class
	$(JAVA) Test

tunnel56.nex tunnel56.obx: kwadrat.lot skosy.lot tabc.dat
tunnel56.obx: taba.dat tabr.dat

tabr.dat taba.dat tabc.dat: Test.class toruseru.dat
	$(JAVA) Test write

.PHONY: clean
clean:
	$(RM) tunnel56.nex tunnel56.obx
	$(RM) taba.dat tabc.dat tabr.dat Test*.class
