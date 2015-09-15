/*
 * Copyright (C) 2015 Pedro Vicente Gomez Sanchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pedrovgs.androidgameboyemulator.core.mmu;

import android.util.Log;

public class MMU {

  private static final String LOGTAG = "MMU";

  private static final int VRAM_BOTTOM_LIMIT = 0x8000;
  private static final int VRAM_TOP_LIMIT = 0xA000;
  private static final int BIOS_LIMIT = 0x100;
  private static final int MEMORY_SPACE = 65536;

  private final byte[] memory;

  private boolean systemReady;
  private MMUListener listener;

  public MMU() {
    this.memory = new byte[MEMORY_SPACE];
    reset();
  }

  public void setListener(MMUListener listener) {
    this.listener = listener;
  }

  public byte readByte(int address) {
    byte value = 0;
    if (!systemReady && address < BIOS_LIMIT) {
      int biosValue = BIOS.ROM[address];
      value = (byte) biosValue;
    } else {
      value = memory[address];
    }
    Log.d(LOGTAG, "READ BYTE -> Byte value = " + value + " Read from " + address);
    return value;
  }

  public int readWord(int address) {
    int firstByte = readByte(address) & 0xFF;
    int secondByte = readByte(address + 1) & 0xFF;
    secondByte = secondByte << 8;
    int value = firstByte + secondByte;
    Log.d(LOGTAG, "READ WORD -> Byte value = " + value + " Read from " + address);
    return value;
  }

  public void writeByte(int address, byte value) {
    memory[address] = value;
    if (address >= VRAM_BOTTOM_LIMIT && address < VRAM_TOP_LIMIT) {
      notifyVRAMUpdated(address, value);
    }
    Log.d(LOGTAG, "WRITE BYTE -> Write byte value = " + value + " written at " + address);
  }

  public void writeWord(int address, int value) {
    byte firstByte = (byte) (value & 0xFF);
    writeByte(address, firstByte);
    byte secondByte = (byte) (value >> 8);
    writeByte(address + 1, secondByte);
    Log.d(LOGTAG, "WRITE WORD -->");
    Log.d(LOGTAG, "Byte value = " + firstByte + " written at " + address);
    Log.d(LOGTAG, "Byte value = " + secondByte + " written at " + address + 1);
  }

  public void reset() {
    for (int i = 0; i < memory.length; i++) {
      memory[i] = 0;
    }
    systemReady = false;
  }

  public boolean isSystemReady() {
    return systemReady;
  }

  public void setSystemReady(boolean systemReady) {
    this.systemReady = systemReady;
    if (systemReady) {
      Log.d(LOGTAG, "BIOS loaded. Let's go to load the game.");
    }
  }

  private void notifyVRAMUpdated(int address, byte value) {
    if (listener != null) {
      listener.onVRAMUpdated(address, value);
    }
  }
}
