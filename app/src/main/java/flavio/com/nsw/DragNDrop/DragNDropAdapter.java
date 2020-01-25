/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flavio.com.nsw.DragNDrop;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import flavio.com.nsw.R;
import flavio.com.nsw.others.GestioneDB;

public final class DragNDropAdapter extends BaseAdapter implements RemoveListener, DropListener{

	private int[] mIds;
    private int[] mLayouts;
    private String[] s;
    private LayoutInflater mInflater;
    private ArrayList<String> mContent;
    private GestioneDB db;

    public DragNDropAdapter(Context context, ArrayList<String> content) {
        init(context,new int[]{android.R.layout.simple_list_item_1},new int[]{android.R.id.text1}, content);
    }
    
    public DragNDropAdapter(Context context, int[] itemLayouts, int[] itemIDs, ArrayList<String> content) {
    	init(context,itemLayouts,itemIDs, content);
    }

    private void init(Context context, int[] layouts, int[] ids, ArrayList<String> content) {
    	// Cache the LayoutInflate to avoid asking for a new one each time.
        db = new GestioneDB(context);
    	mInflater = LayoutInflater.from(context);
    	mIds = ids;
    	mLayouts = layouts;
    	mContent = content;
    }
    
    /**
     * The number of items in the list
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return mContent.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public String getItem(int position) {
        return mContent.get(position);
    }

    /**
     * Use the array index as a unique id.
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, View,
     *      ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(mLayouts[0], null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();

            holder.text = (TextView) convertView.findViewById(mIds[0]);
            holder.reps = (TextView) convertView.findViewById(mIds[1]);
            holder.id = (TextView) convertView.findViewById(mIds[2]);

            holder.delete = convertView.findViewById(R.id.btnRemove);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        String content = mContent.get(position);
        s = content.split("&&");
        holder.text.setText(s[0]);
        if(Integer.parseInt(s[1])>0)
            if(s[3].equals("r"))
                holder.reps.setText("x"+s[1]);
            else
                holder.reps.setText(s[1]+"s");
        else
            holder.reps.setText("MAX");
        holder.id.setText(s[2] + " - " + s[4] + " - "+ s[5]);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(v.getContext())
                        // set message, title, and icon
                        .setTitle("Remove")
                        .setMessage("Do you want to remove the Exercise?")
                        .setIcon(android.R.drawable.ic_menu_delete)

                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.open();
                                Cursor c = db.findRepsSetsByWorkoutIdAndPos(Integer.parseInt(s[4]), Integer.parseInt(s[5]));
                                c.moveToFirst();
                                if(c.getCount()>0 && !c.isNull(c.getColumnIndex(GestioneDB.REPS_SETS_ID))) {
                                    boolean x = db.deleteRepsSetsById(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_ID)));
                                    mContent.remove(position);
                                }
                                dialog.dismiss();
                                notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .show();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView text, reps, id;
        ImageButton delete;
    }

	public void onRemove(int which) {
		if (which < 0 || which > mContent.size()) return;		
		mContent.remove(which);
	}

	public void onDrop(int from, int to) {
		String temp = mContent.get(from);
		mContent.remove(from);
		mContent.add(to,temp);
	}
}