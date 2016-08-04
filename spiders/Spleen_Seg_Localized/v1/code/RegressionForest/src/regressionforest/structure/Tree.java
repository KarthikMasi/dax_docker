package regressionforest.structure;

import regressionforest.context.DataCollection;

//import java.util.List;

public class Tree {
	private Node[] nodes;

	public Tree(int decisionLevels) throws Exception {
		if(decisionLevels<0)
			throw new Exception("Tree can't have less than 0 decision levels.");

		if(decisionLevels>50)
			throw new Exception("Tree can't have more than 50 decision levels.");

		// This full allocation of node storage may be wasteful of memory
		// if trees are unbalanced but is efficient otherwise. Because child
		// node indices can determined directly from the parent node's index
		// it isn't necessary to store parent-child references within the
		// nodes.
		nodes = new Node[(1 << (decisionLevels + 1)) - 1]; 
		for (int n = 0; n < nodes.length; ++n)
			nodes[n] = new Node();
	}

	public Node[] GetNodes(){
		return nodes;
	}
	
	public int GetNodeCount() {
		return nodes.length; 
	}

	public Node GetNode(int index) {
		return nodes[index];
	}

	public void SetNode(int index, Node node) {
		nodes[index] = node;
	}

	public void CheckValid() {
		if (GetNodeCount()==0) throw new RuntimeException("Valid tree must have at least one node.");
		if (GetNode(0).IsNull()) throw new RuntimeException("A valid tree must have non-null root node.");
		
		CheckValidRecurse(0, false);
	}
	
	private final void CheckValidRecurse(int index, boolean bHaveReachedLeaf) {
		if (!bHaveReachedLeaf && GetNode(index).IsLeaf()){
			bHaveReachedLeaf = true;
		}
		else {
			if (bHaveReachedLeaf){
				if (!GetNode(index).IsNull()) 
					throw new RuntimeException("Valid tree must have all descendents of leaf nodes set as null nodes.");
			}
			else {
				if (!GetNode(index).IsSplit())
					throw new RuntimeException("Valid tree must have all antecents of leaf nodes set as split nodes.");
					
			}
		}
		
		if (index >= (GetNodeCount()-1) / 2) {
			if (!bHaveReachedLeaf)
				throw new RuntimeException("Valid tree must have all branches terminated by leaf nodes.");
		}
		else {
	        CheckValidRecurse(2 * index + 1, bHaveReachedLeaf);
	        CheckValidRecurse(2 * index + 2, bHaveReachedLeaf);
		}
	}

	public int[] Apply(DataCollection data)
	{
		CheckValid();

		int[] leafNodeIndices = new int[data.GetDataCount()]; // of leaf node reached per data point

		// Allocate temporary storage for data point indices and response values
		int[] dataIndices_ = new int[data.GetDataCount()];

		for (int i = 0; i < data.GetDataCount(); i++)
			dataIndices_[i] = i;

		float[] responses_ = new float[data.GetDataCount()];

		ApplyNode(0, data, dataIndices_, 0, data.GetDataCount(), leafNodeIndices, responses_);

		return leafNodeIndices;
	}

	private void ApplyNode(
			int nodeIndex,
			DataCollection data,
			int[] dataIndices,
			int i0,
			int i1,
			int[] leafNodeIndices,
			float[] responses_) {
		Node node = nodes[nodeIndex];
	
		if (node.IsLeaf()) {
			for (int i = i0; i < i1; i++)
				leafNodeIndices[dataIndices[i]] = nodeIndex;
			return;
		}
	
		if (i0 == i1)   // No samples left
			return;
	
		for (int i = i0; i < i1; i++){
			responses_[i] = node.feature.GetResponse(data, dataIndices[i]);
		};
	
		int ii = Partition(responses_, dataIndices, i0, i1, node.threshold);
	
		// Recurse for child nodes.
		ApplyNode(nodeIndex * 2 + 1, data, dataIndices, i0, ii, leafNodeIndices, responses_);
		ApplyNode(nodeIndex * 2 + 2, data, dataIndices, ii, i1, leafNodeIndices, responses_);
	}

	public static int Partition(float[] keys, int[] values, int i0, 
			int i1, float threshold) {
		int i = i0;     // index of first element
		int j = i1 - 1; // index of last element

		while (i != j)
		{
			if (keys[i] >= threshold)
			{
				// Swap keys[i] with keys[j]
				float key = keys[i];
				int value = values[i];

				keys[i] = keys[j];
				values[i] = values[j];

				keys[j] = key;
				values[j] = value;

				j--;
			}
			else
			{
				i++;
			}
		}

		return keys[i] >= threshold ? i : i + 1;
	}
}
