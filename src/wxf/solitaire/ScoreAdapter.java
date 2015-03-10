package wxf.solitaire;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScoreAdapter extends ArrayAdapter<ScoreItem>{

	private List<ScoreItem> items;
	public ScoreAdapter(Context context, int textViewResourceId,
			List<ScoreItem> items) {
		super(context, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.items = items;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) SOLITAIREActivity.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.content, null);
		}
		ScoreItem content = items.get(position);
//		Log.v("listcontent", content.timeStr + content.timeused);
		if (content != null) {
			TextView timeused = (TextView) view
					.findViewById(R.id.tv_timeused);
			TextView timestr = (TextView) view
					.findViewById(R.id.tv_timestr);

			timeused.setText((position + 1) + "¡¢ºÄÊ±" + content.timeused + "Ãë");
			timestr.setText(content.timeStr);
		}
		return view;
	}

}
