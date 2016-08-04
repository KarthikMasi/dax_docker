package regressionforest.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import regressionforest.context.DataCollection;
import regressionforest.matrix.MatrixComputor;
import regressionforest.structure.Forest;
import regressionforest.structure.Tree;
import regressionforest.utility.Histogram;

public class ForestTesterHistPlus {
	public static double[][] TestForest(DataCollection data, Forest forest, int classCount, int dimCount, double confidentDataPercentage) throws Exception{
		if (forest == null) throw new IllegalArgumentException("Forest does not exist.");

		double[] vc = new double[dimCount];
		double[] dc = new double[dimCount];
		double[] bc = new double[dimCount];
		double[][] box = new double[classCount][dimCount];
		ArrayList<ArrayList<Double>> BC = new ArrayList<ArrayList<Double>>();
		for (int d = 0; d < dimCount; d++) BC.add(new ArrayList<Double>());

		int[][] leafNodeIndices = forest.Apply(data);

		for (int c = 0; c < classCount; c++){
			for (int d = 0; d < dimCount; d++) BC.get(d).clear();
			for (int t = 0; t < forest.GetTreeCount(); t++){
				Tree tree = forest.GetTree(t);
				double[] dataUncertainty = new double[data.GetDataCount()];
				double[] dataUncertaintySort = new double[data.GetDataCount()];
				for (int i = 0; i < data.GetDataCount(); i++){
					dataUncertainty[i] = tree.GetNode(leafNodeIndices[t][i]).statisticsRecord.GetUncertainty()[c];
					dataUncertaintySort[i] = dataUncertainty[i];
				}
				Arrays.sort(dataUncertaintySort);
				double thresh = dataUncertaintySort[(int) Math.round(confidentDataPercentage * data.GetDataCount())];
				for (int i = 0; i < data.GetDataCount(); i++){
					if (dataUncertainty[i] <= thresh){
						vc = data.GetVector(i);
						dc = tree.GetNode(leafNodeIndices[t][i]).statisticsRecord.GetMean()[c];
						bc = MatrixComputor.Substract(vc, dc);
						// collect in the list
						for (int d = 0; d < dimCount; d++)
							BC.get(d).add(bc[d]);
					}
				}
			}

			for (int d = 0; d < dimCount; d++) {
				double highValue = Math.ceil(Collections.max(BC.get(d)));
				double lowValue = Math.floor(Collections.min(BC.get(d)));
				int nBins = (int) ((highValue-lowValue)/3); //
				Histogram hist = new Histogram(BC.get(d), nBins, lowValue, highValue);
				box[c][d] = hist.getMaxCountBinMean();
			}
		}
		return box;	
	}

	public static List<LeafID> SortForestLeafNodeByUncertainty(Forest forest, int classIndex){
		List<LeafID> ids = new ArrayList<LeafID>();
		// add all leaf node
		for (int t = 0; t < forest.GetTreeCount(); ++t){
			Tree tree = forest.GetTree(t);
			for (int n = 0; n < tree.GetNodeCount(); ++n) {
				// skip if it is a null node
				if (tree.GetNode(n).IsNull()) continue;
				// skip if it is a split node
				if (!tree.GetNode(n).IsLeaf()) continue;
				// skip if it has been selected in the list				
				double uncertainty = tree.GetNode(n).statisticsRecord.GetUncertainty()[classIndex];
				LeafID id = new LeafID(t, n, uncertainty);
				ids.add(id);
			}
		}
		// sort
		Collections.sort(ids);
		return ids;
	}

	public static LeafID GetNextNodeIdWithLeastUncertainty(Forest forest, int classIndex, List<LeafID> selectedNodeId) throws Exception {
		double min = Double.MAX_VALUE;
		boolean flagSelected = false;
		double uncertainty;

		LeafID id = new LeafID();
		for (int t = 0; t < forest.GetTreeCount(); ++t){
			Tree tree = forest.GetTree(t);
			for (int n = 0; n < tree.GetNodeCount(); ++n) {
				flagSelected = false;
				// skip if it is a null node
				if (tree.GetNode(n).IsNull()) continue;
				// skip if it is a split node
				if (!tree.GetNode(n).IsLeaf()) continue;
				// skip if it has been selected in the list
				for (int i = 0; i < selectedNodeId.size(); ++i)
					if (t == selectedNodeId.get(i).TreeID && n == selectedNodeId.get(i).NodeID)
					{flagSelected = true;break;}
				if (flagSelected) continue;
				// compare the uncertainty, and update if necessary
				uncertainty = tree.GetNode(n).statisticsRecord.GetUncertainty()[classIndex];
				if (min > uncertainty){
					min = uncertainty;
					id = new LeafID(t,n, uncertainty);
				}
			}
		}
		if (id.TreeID == -1 || id.NodeID == -1) throw new Exception("cannot find the next node with least uncertainty.");
		return id;
	}


	public static class LeafID implements Comparable <LeafID>{
		public int TreeID;
		public int NodeID;
		public double Uncertainty; 

		public LeafID(){
			TreeID = -1;
			NodeID = -1;
			Uncertainty = Double.MAX_VALUE;
		}
		public LeafID(int TreeID, int NodeID, double Uncertainty){
			this.TreeID = TreeID;
			this.NodeID = NodeID;
			this.Uncertainty = Uncertainty;
		}
		public int compareTo(LeafID leafid) {
			return (int) (this.Uncertainty - leafid.Uncertainty);
		}
	}

	public static void main(String[] args){
		//		List<LeafID> ids = new ArrayList<LeafID>();
		//		LeafID id = new LeafID(0,1,100);
		//		ids.add(id);
		//		id = new LeafID(1,0,300);
		//		ids.add(id);
		//		id = new LeafID(2,3,200);
		//		ids.add(id);
		//		
		//		Collections.sort(ids);
		//		for (int i = 0; i < ids.size(); i++){
		//			LeafID tmp = ids.get(i);
		//			System.out.println("t: " + tmp.TreeID + " n: " + tmp.NodeID + " u: " + tmp.Uncertainty);
		//		}	

//		double[] ar = {1,4,50,2,5,10};
//		Arrays.sort(ar);
//		for (int i = 0; i < ar.length; i++){
//			System.out.println(ar[i]);
//		}	
	}


}
