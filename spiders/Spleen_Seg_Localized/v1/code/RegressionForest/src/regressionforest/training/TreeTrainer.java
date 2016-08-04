package regressionforest.training;

import regressionforest.context.DataCollection;
import regressionforest.context.TrainingContext;
import regressionforest.structure.Tree;

public class TreeTrainer {
	static public Tree TrainTree(TrainingContext context, TrainingParameters parameters, DataCollection data) throws Exception {
		TreeTrainingOperation trainingOperation = new TreeTrainingOperation(context, parameters, data);

		Tree tree = new Tree(parameters.MaxDecisionLevels);

		trainingOperation.TrainNodesRecurse(tree.GetNodes(), 0, 0, data.GetDataCount(), 0);  // will recurse until termination criterion is met

		tree.CheckValid();

		return tree;
	}
}
