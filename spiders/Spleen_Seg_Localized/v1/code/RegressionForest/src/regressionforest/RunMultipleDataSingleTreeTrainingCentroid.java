package regressionforest;

import regressionforest.context.DataCollection;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Forest;
import regressionforest.training.ParallelForestTrainer;
import regressionforest.training.TrainingParameters;
import regressionforest.utility.BoundaryFinder;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class RunMultipleDataSingleTreeTrainingCentroid {
	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			System.out.println("Five input arguments are required exactly.");
			System.out.println("1. image directory");
			System.out.println("2. label directory");
			System.out.println("3. xml to save path");
			System.out.println("4. class count");
			System.out.println("5. dimension count");
			return;
		}		
		// arguments
		String img_dir = args[0];
		String label_dir = args[1];
		String xmlfn = args[2];
		int classCount = Integer.parseInt(args[3]);
		int dimCount = Integer.parseInt(args[4]);

		String img_suffix = "_rawimg.nii.gz";
		String label_suffix = "_organlabel.nii.gz";
		String[] imgFiles = FilesSearchBySuffix.FilesSearch(img_dir, img_suffix);
		DataCollection dc = new DataCollection(classCount, dimCount);
		int[] value = new int[12];
		for (int v = 0; v < value.length; v++) {value[v] = v + 1;}

		// load data
		long tBegin = System.currentTimeMillis();
		for (int i = 0; i < imgFiles.length; i++){
			String fn = imgFiles[i].substring(0, imgFiles[i].indexOf("_"));
			String imgfn = img_dir + imgFiles[i];
			String labelfn = label_dir + fn + label_suffix;

			int[][][] img = niftiIO.niftiImgLoad(imgfn);
			int[][][] label = niftiIO.niftiImgLoad(labelfn);
			float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
			float[][] box = BoundaryFinder.GetCentroid(label, value, voxres);		
			dc.AddDataItem(img, box, voxres);
		}
		System.out.println("Data Loaded: " + dc.GetDataItemCount());
		
		// training
		TrainingContext context = new TrainingContext(dc.GetClassCount(), dc.GetDimCount());
		TrainingParameters parameters = new TrainingParameters();
		parameters.NumberOfTrees = 1;
		Forest forest = ParallelForestTrainer.TrainForest(context, parameters, dc);

		// saving
		XmlIO xIO = new XmlIO();
		xIO.SaveForestAsDocuemnt(forest,xmlfn);
		System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000 / 60) + " minutes");	

	}		

}
