package testerObjects;

import java.util.ArrayList;
import java.util.List;

public class TesterMoneyRegister {
	
	private List<TesterReceipt> receiptsPerStudent;
	private int totalIncome;

	public TesterMoneyRegister() {
		receiptsPerStudent = new ArrayList<TesterReceipt>();
		totalIncome = 0;
	}

	/**
	 * @return the receiptPerStudent
	 */
	public List<TesterReceipt> getReceiptsPerStudent() {
		return receiptsPerStudent;
	}

	/**
	 * @param receiptsPerStudent the receiptPerStudent to set
	 */
	public void addReceiptsPerStudent(TesterReceipt receipt) {
		receiptsPerStudent.add(receipt);
	}

	/**
	 * @return the totalIncome
	 */
	public int getTotalIncome() {
		return totalIncome;
	}

	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(int totalIncome) {
		this.totalIncome = totalIncome;
	}


}
