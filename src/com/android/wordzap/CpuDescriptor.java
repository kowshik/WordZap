/**
 * 
 * The MIT License : http://www.opensource.org/licenses/mit-license.php

 * Copyright (c) 2010 Kowshik Prakasam

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.android.wordzap;

/*
 * 
 * Describes the opponent in the game through the following attributes :
 * 
 * (1) cpuMoveTime - How long does the opponent take to make cpuMoveFreq moves ?
 * (2) cpuMoveFreq - How many times does the opponent move within a particular time interval defined by cpuMoveTime ?
 * (3) cpuZapFreq - How many times does the opponent zap words within a particular time interval defined by cpuZapTime ?
 * (4) cpuZapTime - How long does the opponent take to make cpuZapFreq zaps ?
 * 
 */


import com.android.wordzap.exceptions.InvalidCpuDescriptionException;

public class CpuDescriptor implements Cloneable {
	private int cpuMoveFreq;
	private int cpuMoveTime;
	private int cpuZapFreq;
	private int cpuZapTime;

	public int getCpuMoveFreq() {
		return cpuMoveFreq;
	}

	public void setCpuMoveFreq(int cpuMoveFreq) {
		this.cpuMoveFreq = cpuMoveFreq;
	}

	public int getCpuMoveTime() {
		return cpuMoveTime;
	}

	public void setCpuMoveTime(int cpuMoveTime) {
		this.cpuMoveTime = cpuMoveTime;
	}

	public int getCpuZapFreq() {
		return cpuZapFreq;
	}

	public void setCpuZapFreq(int cpuZapFreq) {
		this.cpuZapFreq = cpuZapFreq;
	}

	public int getCpuZapTime() {
		return cpuZapTime;
	}

	public void setCpuZapTime(int cpuZapTime) {
		this.cpuZapTime = cpuZapTime;
	}

	public CpuDescriptor(int cpuMoveTime, int cpuMoveFreq, int cpuZapTime,
			int cpuZapFreq) throws InvalidCpuDescriptionException {

		if (cpuMoveTime > 0) {
			this.cpuMoveTime = cpuMoveTime;
		} else {
			throw new InvalidCpuDescriptionException(
					"CPU Move time interval has to be greater than zero.");
		}

		// cpuMoveFreq cant be negative
		if (cpuMoveFreq > 0) {
			this.cpuMoveFreq = cpuMoveFreq;
		} else {
			throw new InvalidCpuDescriptionException(
					"CPU Move Frequency has to be greater than zero.");
		}

		// cpuZapTime cant be negative
		if (cpuZapTime > 0) {
			this.cpuZapTime = cpuZapTime;
		} else {
			throw new InvalidCpuDescriptionException(
					"CPU Zap time interval has to be greater than zero.");
		}

		// cpuZapFreq cant be negative
		if (cpuZapFreq > 0) {
			this.cpuZapFreq = cpuZapFreq;
		} else {
			throw new InvalidCpuDescriptionException(
					"CPU Zap Frequency has to be greater than zero.");
		}

	}

	@Override
	public CpuDescriptor clone() {
		CpuDescriptor clone=null;
		try {
			clone = (CpuDescriptor)super.clone();
			
		} catch (CloneNotSupportedException e) {
			
		}
		return clone;

	}

}
