package com.github.pedrovgs.androidgameboyemulator.core.processor.isa;

import com.github.pedrovgs.androidgameboyemulator.core.processor.GBZ80;

class LoadAIntoL extends Instruction {

  LoadAIntoL(GBZ80 z80) {
    super(z80);
  }

  @Override public void execute() {
    int registerA = z80.getRegisterA();
    z80.setRegisterL(registerA);
    setLastExecutionTime(1);
  }
}