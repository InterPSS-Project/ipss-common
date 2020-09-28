package org.interpss.numeric.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.numeric.sparse.ISparseEqnDouble;

public class MatrixOutputUtil {
	
 public static String matrixToString(ISparseEqnComplex sparseMatrix){
		StringBuffer sb = new StringBuffer();
		int dim=sparseMatrix.getDimension();
		sb.append(" j = sqrt(-1);\n");
		sb.append(" matrix = sparse("+dim+");\n");
		//NOTE: matlab is 1-based index, i.e., index starting from 1.
		//while sparse matrix used in InterPSS are all 0-based index, 
		//thus the indices need to add one during output.
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				if(sparseMatrix.getA(i, j)!=null && sparseMatrix.getA(i, j).abs()>0)
				   sb.append("matrix("+Number2String.toFixLengthStr("#####0", i+1)+","+
						   Number2String.toFixLengthStr("#####0", j+1)+")  = "
						   +Number2String.toFixLengthStr(sparseMatrix.getA(i, j).getReal(),"#######.0000")
						   +" + j*("+Number2String.toFixLengthStr(sparseMatrix.getA(i, j).getImaginary(),"#######.0000")+");\n");
			}
		}
		return sb.toString();

	}

 public static boolean matrixToMatlabMFile(String mFileName,ISparseEqnComplex sparseMatrix){
		StringBuffer sb = new StringBuffer();
		int dim=sparseMatrix.getDimension();
		sb.append(" j = sqrt(-1);\n");
		sb.append(" matrix = sparse("+dim+");\n");
		//NOTE: matlab is 1-based index, i.e., index starting from 1.
		//while sparse matrix used in InterPSS are all 0-based index, 
		//thus the indices need to add one during output.
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				if(sparseMatrix.getA(i, j)!=null && sparseMatrix.getA(i, j).abs()>0)
				   sb.append("matrix("+Number2String.toFixLengthStr("#####0", i+1)+","+
						   Number2String.toFixLengthStr("#####0", j+1)+")  = "
						   +Number2String.toFixLengthStr(sparseMatrix.getA(i, j).getReal(),"#######.0000")
						   +" + j*("+Number2String.toFixLengthStr(sparseMatrix.getA(i, j).getImaginary(),"#######.0000")+");\n");
			}
		}
		
//		sb.append("\n\n");
//		for(int i=0;i<dim;i++){
//			sb.append("B("+i+") = " +Number2String.toFixLengthStr(sparseMatrix..getReal(),"#######.0000")
//					   +" + j*("+Number2String.toFixLengthStr(sparseMatrix.getA(i, j).getImaginary(),"#######.0000")+");\n");
//		}
//		
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(mFileName));
			out.write(sb.toString().getBytes());
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}
	
 public static  boolean matrixToMatlabMFile(String mFileName,ISparseEqnDouble sparseMatrix){
    	StringBuffer sb = new StringBuffer();
		int dim=sparseMatrix.getDimension();
		sb.append(" j = sqrt(-1);\n");
		sb.append(" matrix = sparse("+dim+");\n");
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				if(sparseMatrix.getAij(i, j)!=0)
				   sb.append("matrix("+Number2String.toFixLengthStr("#####0", i+1)+","+
						   Number2String.toFixLengthStr("#####0", j+1)+") = "
						   +Number2String.toFixLengthStr(sparseMatrix.getAij(i, j),"#######.0000")+";\n");
			}
		}
		
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(mFileName));
			out.write(sb.toString().getBytes());
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}
}
