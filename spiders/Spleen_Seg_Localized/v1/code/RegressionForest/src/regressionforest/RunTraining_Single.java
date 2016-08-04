package regressionforest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import regressionforest.context.DataCollection;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Forest;
import regressionforest.training.ParallelForestTrainer;
import regressionforest.training.TrainingParameters;
import regressionforest.utility.BoundaryFinder;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class RunTraining_Single {
	public static void main(String[] args) throws Exception {
		if (args.length != 9 && args.length != 15) {
			System.out.println("Either 9 or 15 input arguments are required.");
			System.out.println("1. image directory");
			System.out.println("2. image suffix");
			System.out.println("3. label directory");
			System.out.println("4. label suffix");
			System.out.println("5. xml to save path");
			System.out.println("6. class count");
			System.out.println("7. dimension count");
			System.out.println("8. tree depth");
			System.out.println("9. tree number");
			System.out.println("10 ~ 12. feature box offsets (x, y, z)");
			System.out.println("13 ~ 15. feature box sizes (x, y, z)");
			return;
		}		
		
		long tBegin = System.currentTimeMillis();
		// arguments
		String img_dir = args[0];
		String img_suffix = args[1];
		String label_dir = args[2];
		String label_suffix = args[3];
		String xmlfn = args[4];
		
		int classCount = 1;
		classCount = Integer.parseInt(args[5]);
		int dimCount = Integer.parseInt(args[6]);
		int treeDepth = Integer.parseInt(args[7]);
		int treeNumber = Integer.parseInt(args[8]);

		String[] imgFiles = FilesSearchBySuffix.FilesSearch(img_dir, img_suffix);
		String[] labelFiles = FilesSearchBySuffix.FilesSearch(label_dir, label_suffix);
		if (imgFiles.length != labelFiles.length) {
			System.out.println("image and label files may not match!");
			return;
		}
		
		int[] value = {1};
		
//		// get unique values excluding zero
//		int[][][] tmp = niftiIO.niftiImgLoad(label_dir + labelFiles[0]);
//		ArrayList<Integer> tmplabellist = new ArrayList<Integer>();
//		for (int i = 0; i < tmp.length; i++)
//			for (int j = 0; j < tmp[0].length; j++)
//				for (int k = 0; k < tmp[0][0].length; k++)
//					tmplabellist.add(tmp[i][j][k]);
//		Set<Integer> uniquelabelset = new HashSet<Integer>(tmplabellist);
//		uniquelabelset.remove(0);
//		Object[] uniquelabelObject = uniquelabelset.toArray();
//		int[] value = new int [uniquelabelset.size()];
//		for (int i = 0; i < uniquelabelset.size(); i++){
//			value[i] = ((Integer) uniquelabelObject[i]).intValue();
//			System.out.println(value[i]);
//		}
		
		// load data
		DataCollection dc = new DataCollection(classCount, dimCount);
		for (int i = 0; i < imgFiles.length; i++){
			String imgfn = img_dir + imgFiles[i];
			String labelfn = label_dir + labelFiles[i];
			int[][][] img = niftiIO.niftiImgLoad(imgfn);
			int[][][] label = niftiIO.niftiImgLoad(labelfn);
			float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
			float[][] box;
			if (dimCount == 3) box = BoundaryFinder.GetCentroid(label, value, voxres);
			else box = BoundaryFinder.GetBoundry(label, value, voxres);
			dc.AddDataItem(img, box, voxres);
		}
		System.out.println("Data Loaded: " + dc.GetDataItemCount());
		
		// training
		TrainingParameters parameters = new TrainingParameters();
		parameters.NumberOfTrees = treeNumber;
		parameters.MaxDecisionLevels = treeDepth;
		
		float[] featureBoxOffsets = {0,0,0};
		float[] featureBoxSizes = {0,0,0};
		if (args.length == 15){
			featureBoxOffsets[0] = Float.parseFloat(args[9]);
			featureBoxOffsets[1] = Float.parseFloat(args[10]);
			featureBoxOffsets[2] = Float.parseFloat(args[11]);
			featureBoxSizes[0] = Float.parseFloat(args[12]);
			featureBoxSizes[1] = Float.parseFloat(args[13]);
			featureBoxSizes[2] = Float.parseFloat(args[14]);
		}		
		TrainingContext context = new TrainingContext(dc.GetClassCount(), dc.GetDimCount(),featureBoxOffsets,featureBoxSizes);
		Forest forest = ParallelForestTrainer.TrainForest(context, parameters, dc);

		// saving
		XmlIO xIO = new XmlIO();
		xIO.SaveForestAsDocuemnt(forest,xmlfn);
		System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000 / 60) + " minutes");	
	}		
}
