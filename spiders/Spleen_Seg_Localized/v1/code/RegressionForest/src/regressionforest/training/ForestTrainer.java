package regressionforest.training;

import regressionforest.context.DataCollection;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Forest;
import regressionforest.structure.Tree;

public class ForestTrainer {
	
	public static Forest TrainForest(TrainingContext context, TrainingParameters parameters, DataCollection data) 
			throws Exception {
		
		Forest forest = new Forest();
		for (int t = 0; t < parameters.NumberOfTrees; t++) {
			System.out.println("Training Tree No. " + t);
			Tree tree = TreeTrainer.TrainTree(context, parameters, data);
			forest.AddTree(tree);
		}
		return forest;
	}
}
