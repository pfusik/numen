include ../common.mak

.PHONY: run
run: plasma2.obx
	$(RUNOBX) $<

plasma2.nex plasma2.obx: plasma2.map tunnelz.ang tunnelz.ray

plasma2.map: PcxToMap.class tqa.pcx
	$(JAVA) PcxToMap

PcxToMap.class: ../PCXImage.class

tunnelz.ang: ../flash/tunnelz.ang
	cp ../flash/tunnelz.ang .

tunnelz.ray: ../flash/tunnelz.ray
	cp ../flash/tunnelz.ray .

../flash/tunnelz.ang ../flash/tunnelz.ray:
	$(MAKE) -C ../flash tunnelz.ang tunnelz.ray

.PHONY: clean
clean:
	$(RM) plasma2.nex plasma2.obx
	$(RM) plasma2.map PcxToMap.class
	$(RM) tunnelz.ang tunnelz.ray
