package regressionforest.context;

import regressionforest.training.StatisticsAggregator;
import regressionforest.training.StatisticsRecord;

public class TrainingContext {
	private int classCount;
	private int dimCount;
	private float [] featureBoxOffsets;
	private float [] featureBoxSizes;

	public TrainingContext (int c, int d) {
		classCount = c;
		dimCount = d;
		featureBoxOffsets = new float[3];
		featureBoxSizes = new float[3];
		for (int i = 0; i < 3; i++){
			featureBoxOffsets[i] = 0;
			featureBoxSizes[i] = 0;
		}
	}

	public TrainingContext (int c, int d, float[] offsets, float[] sizes) {
		classCount = c;
		dimCount = d;
		featureBoxOffsets = new float[3];
		featureBoxSizes = new float[3];
		for (int i = 0; i < 3; i++){
			featureBoxOffsets[i] = offsets[i];
			featureBoxSizes[i] = sizes[i];
		}	
	}

	public float[] GetFeatureBoxOffsets(){
		return featureBoxOffsets;
	}

	public float[] GetFeatureBoxSizes(){
		return featureBoxSizes;
	}

	public Feature GetRandomFeature(float xDataRange, float yDataRange, float zDataRange) {
		return new Feature(xDataRange, yDataRange, zDataRange);
	}

	public Feature GetRandomFeature() {
		return new Feature(featureBoxOffsets[0], featureBoxOffsets[1], featureBoxOffsets[2],
				featureBoxSizes[0], featureBoxSizes[1], featureBoxSizes[2]);
	}

	public StatisticsAggregator GetStatisticsAggregator() {
		return new StatisticsAggregator(classCount,dimCount);
	}

	public double ComputeInformationGain(
			StatisticsAggregator allStatistics,
			StatisticsAggregator leftStatistics,
			StatisticsAggregator rightStatistics) {
		double entropyBefore = ((StatisticsAggregator)(allStatistics)).Entropy();

		StatisticsAggregator leftLineFitStats = (StatisticsAggregator)(leftStatistics);
		StatisticsAggregator rightLineFitStatistics = (StatisticsAggregator)(rightStatistics);

		int nTotalSamples = leftLineFitStats.GetSampleCount() + rightLineFitStatistics.GetSampleCount();

		double entropyAfter = (
				leftLineFitStats.GetSampleCount() * leftLineFitStats.Entropy() + 
				rightLineFitStatistics.GetSampleCount() * rightLineFitStatistics.Entropy()
				) / nTotalSamples;

		return entropyBefore - entropyAfter;
	}

	public Boolean ShouldTerminate(double gain,int sampleCounts) {
		return (gain < 0.05 || sampleCounts < 25);
	}
}
