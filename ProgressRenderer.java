import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

//renders progress bar in a table cell
public class ProgressRenderer extends JProgressBar
implements TableCellRenderer{
	
	//generate constructor
	public ProgressRenderer(int min, int max){
		super(min,max);
	}
	@Override
	//invoked when JTable instance renders a cell
	public Component getTableCellRendererComponent(JTable table, 
			Object value, boolean isSelected, boolean hasFocus, 
			int row, int column) {
		//set progressbar's percent completion
		setValue((int) ((Float) value).floatValue());
		return this;
	}
}
