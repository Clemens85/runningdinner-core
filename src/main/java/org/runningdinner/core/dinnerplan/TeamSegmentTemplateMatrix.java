package org.runningdinner.core.dinnerplan;

import java.util.Set;

import org.runningdinner.core.MealClass;

public class TeamSegmentTemplateMatrix {

	private Set<MealClass> meals;

	public TeamSegmentTemplateMatrix(Set<MealClass> meals) {
		this.meals = meals;
	}

	public int[][][] getTemplateMatrix(int teamSegmentSize) {
		if (teamSegmentSize % meals.size() != 0 && teamSegmentSize < meals.size() * meals.size()) {
			throw new IllegalArgumentException("Passeds team segment size must be multiple of " + meals.size() + " and must be at least "
					+ (meals.size() * meals.size()));
		}

		// Currently we support following team segment sizes:
		// 9, 12, 15 (for 3 meals)
		// 4, 6 (for 2 meals)

		switch (teamSegmentSize) {
			case 9:
				return build9Matrix();
			case 12:
				return build12Matrix();
			case 15:
				return build15Matrix();
			case 4:
				return build4Matrix();
			case 6:
				return build6Matrix();
			default:
				throw new IllegalArgumentException("teamSegmentSize must be one of the following values: 9, 12, 15, 4, 6");
		}
	}

	protected int[][][] build9Matrix() {
		int[][][] result = new int[][][] {
			{ 
				{ 1, 4, 7 }, // 1 is hoster for 4 and 7 
				{ 2, 5, 8 }, // 2 is hoster for 5 and 8
				{ 3, 6, 9 }  // 3 is hoster for 6 and 9
				// This complete block represents all hosters for one meal, e.g. APPETIZER
			}, 
			{ 
				{ 4, 2, 9 },  // 4 is hoster for 2 and 9
				{ 5, 3, 7 },  // ...
				{ 6, 1, 8 } 
			}, 
			{ 
				{7, 2, 6}, 
				{8, 3, 4}, 
				{9, 1, 5} 
			}
		};		
		return result;
	}
	
	protected int[][][] build12Matrix() {
		int[][][] result = new int[][][] {
				{ 
					{ 1, 5, 9 }, 
					{ 2, 6, 10 }, 
					{ 3, 7, 11 },
					{ 4, 8, 12 }
				}, 
				{ 
					{ 5, 10, 11 },
					{ 6, 9, 12 },
					{ 7, 1, 4 },
					{ 8, 2, 3 }
				}, 
				{ 
					{ 9, 2, 7 }, 
					{ 10, 1, 8 }, 
					{ 11, 4, 6 },
					{ 12, 3, 5 }
				}
			};		
			return result;
	}
	
	protected int[][][] build15Matrix() {
		throw new UnsupportedOperationException("nyi");
	}
	

	protected int[][][] build4Matrix() {
		int[][][] result = new int[][][] {
			{ 
				{ 1, 3}, 
				{ 2, 4}
			}, 
			{ 
				{ 3, 2}, 
				{ 4, 1 }
			}
		};		
		return result;
	}
	
	protected int[][][] build6Matrix() {
		throw new UnsupportedOperationException("nyi");
	}
}
