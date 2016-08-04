package regressionforest.structure;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import regressionforest.context.DataCollection;


public class Forest {
	private List<Tree> trees = new ArrayList<Tree>();
	private int[][] leafNodeTreeIndices;
	
	public void AddTree(Tree tree) {
		tree.CheckValid();
		trees.add(tree);
	}
	
	public Tree GetTree(int index) {
		return trees.get(index);
	}
	
	public int GetTreeCount() {
		return trees.size();
	}
	
	public int[][] Apply(DataCollection data) throws InterruptedException {
		leafNodeTreeIndices = new int[GetTreeCount()][];
		int maxCountOfThreads = 2;
		int nCpus = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(Math.min(maxCountOfThreads, nCpus));
		// Iterate over trees	
		for (int t = 0; t < GetTreeCount(); t++) {
				ApplyTreeRunnable r = new ApplyTreeRunnable(t,data);
				pool.execute(r);
		}
		pool.shutdown();
		while (!pool.isTerminated()){}
//		for (int t = 0; t < GetTreeCount(); t++)
//			leafNodeTreeIndices[t] = trees.get(t).Apply(data);
		return leafNodeTreeIndices;
	}
	class ApplyTreeRunnable implements Runnable{
		private int t;
		private DataCollection data_;
		
		public ApplyTreeRunnable(int treeIndex, DataCollection data){
			t = treeIndex;
			data_ = data;  // allocate the same memory since just for reading here 
		}
		public void run(){
			leafNodeTreeIndices[t] = trees.get(t).Apply(data_);
		}
	}
}
