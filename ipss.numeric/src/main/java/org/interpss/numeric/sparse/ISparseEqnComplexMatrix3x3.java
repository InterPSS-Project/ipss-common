package org.interpss.numeric.sparse;

import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.datatype.Complex3x3;
import org.interpss.numeric.sparse.base.ISparseEqnObject;

public interface ISparseEqnComplexMatrix3x3 extends ISparseEqnObject<Complex3x3, Complex3x1>{

	ISparseEqnComplex getSparseEqnComplex();

}