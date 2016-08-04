package regressionforest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import regressionforest.context.DataCollection;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Forest;
import regressionforest.training.ParallelForestTrainer;
import regressionforest.training.TrainingParameters;
import regressionforest.utility.Nifti1Dataset;
import regressionforest.utility.XmlIO;

public class RunSingleDataSingleTreeTraining {	
	public static void main(String[] args) throws Exception {
		if (args.length != 6) {
			System.out.println("Six input arguments are required exactly.");
			System.out.println("1. image path");
			System.out.println("2. label path");
			System.out.println("3. xml to save path");
			System.out.println("4. class count");
			System.out.println("5. dimension count");
			System.out.println("6. number of tree");
			return;
		}		
		// arguments
		int classCount = Integer.parseInt(args[3]);
		int dimCount = Integer.parseInt(args[4]);
		int numberOfTree = Integer.parseInt(args[5]);
		int[][][] img = null;
		float[][] box = new float[classCount][dimCount];
		float[] voxres = new float[3];
		
		String pathImg = args[0];
		String pathLabel = args[1];
		String xmlfn = args[2];
		
		/*************************************************/
		System.out.println("Load data...");
		// load image
		Nifti1Dataset nds = new Nifti1Dataset(pathImg);
		nds.readHeader();
		double[][][] tmp = nds.readDoubleVol((short) 0);
		img = new int[tmp[0][0].length][tmp[0].length][tmp.length];
		for (int i = 0; i < tmp.length; i++)
			for (int j = 0; j < tmp[0].length; j++)
				for (int k = 0; k < tmp[0][0].length; k++)
					img[k][j][i] = (int)(tmp[i][j][k]);

		// get voxres
		voxres[0] = nds.pixdim[1];
		voxres[1] = nds.pixdim[2];
		voxres[2] = nds.pixdim[3];
		
		// load label
		nds = new Nifti1Dataset(pathLabel);
		nds.readHeader();
		tmp = nds.readDoubleVol((short) 0);
		int[][][] label = new int[tmp[0][0].length][tmp[0].length][tmp.length];
		for (int i = 0; i < tmp.length; i++)
			for (int j = 0; j < tmp[0].length; j++)
				for (int k = 0; k < tmp[0][0].length; k++)
					label[k][j][i] = (int)(tmp[i][j][k]);

		// get boundaries
		List<Integer> ix = new ArrayList<Integer>();
		List<Integer> iy = new ArrayList<Integer>();
		List<Integer> iz = new ArrayList<Integer>();
		
		for (int c = 0; c < classCount; c++) {
			ix.clear();
			iy.clear();
			iz.clear();
			for (int i = 0; i < label.length; i++)
				for (int j = 0; j < label[0].length; j++)
					for (int k = 0; k < label[0][0].length; k++)
						if (label[i][j][k] == c+1) {
							ix.add(i);
							iy.add(j);
							iz.add(k);
						}
			box[c][0] = (Collections.min(ix) + 1) * voxres[0];
			box[c][1] = (Collections.max(ix) + 1) * voxres[0];
			box[c][2] = (Collections.min(iy) + 1) * voxres[1];
			box[c][3] = (Collections.max(iy) + 1) * voxres[1];
			box[c][4] = (Collections.min(iz) + 1) * voxres[2];
			box[c][5] = (Collections.max(iz) + 1) * voxres[2];
		}
		/*************************************************/
		System.out.println("Training...");
		long tBegin = System.currentTimeMillis();
		DataCollection dc = new DataCollection(classCount, dimCount);
		dc.AddDataItem(img, box, voxres);

		TrainingContext context = new TrainingContext(dc.GetClassCount(), dc.GetDimCount());
		TrainingParameters parameters = new TrainingParameters();
		parameters.NumberOfTrees = numberOfTree;
		Forest forest = ParallelForestTrainer.TrainForest(context, parameters, dc);

		XmlIO xIO = new XmlIO();
		xIO.SaveForestAsDocuemnt(forest,xmlfn);
		System.out.println("Training cost: " + 
		Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000 / 60) + " minutes");		
	}
}
