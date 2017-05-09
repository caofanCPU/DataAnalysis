package com.xyz.cf;

import org.junit.Test;
import org.ujmp.core.Matrix;

public class TestUnit {
	
	@Test
	public void testMatrixEle() {
		double[][] coor = { {10, 20, 30, 40, 50, 60, 70, 80, 90, 100},
						  	{10, 20, 30, 40, 50, 60, 70, 80, 90, 100} 
						  };
		Matrix m = Matrix.Factory.importFromArray(coor);
		System.out.println(m.getAsDouble(0,0));
		System.out.println(m.getAsDouble(0,1));
		
	}
}
