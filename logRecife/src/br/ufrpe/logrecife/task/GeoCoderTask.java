package br.ufrpe.logrecife.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import br.ufrpe.logrecife.R;
import br.ufrpe.logrecife.SearchListFragment;

//TODO done
public class GeoCoderTask extends AsyncTask<Void, Void, Void> {
	protected Context context;
	static GeoCoderTask sTask;
	protected FragmentManager fm;
	protected String mLookUpAddress;
	protected Address mResultAddress;
	protected ArrayList<Address> mListAddress = new ArrayList<Address>();
	protected ProgressDialog searching;

	public GeoCoderTask(Context c, String address){
		context = c;
		mLookUpAddress = address;
		searching = new ProgressDialog(context);
	}

	public static GeoCoderTask getInstance(Context c, String address){
		sTask = new GeoCoderTask(c, address);
		return sTask;
	}

	@Override
	protected void onPreExecute(){
		searching.setMessage("Procurando...");
		searching.setCancelable(false);
		searching.show();

	}

	@Override
	protected Void doInBackground(Void... params) {
		Geocoder geocoder = new Geocoder(context);
		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocationName(mLookUpAddress + " Recife", 1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(addresses != null && addresses.size() > 0){
			for(int i = 0; i < addresses.size(); i += 1){
				mListAddress.add(addresses.get(i));
			}
		}

		return null;

	}

	@Override
	protected void onPostExecute(Void object) {
		searching.dismiss();
		if (mListAddress.size()>1) {
			SearchListFragment list = SearchListFragment.newInstance(mListAddress);
			
			((FragmentActivity) context).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.activity_logrecife_container, list, "list");
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commitAllowingStateLoss();
		}
		
		else if (mListAddress.size() == 1){
			RequestTask task = RequestTask.getInstance(context, mListAddress.get(0));
			task.execute();
		}
		
		else{
			Toast.makeText(context, "Nenhum resultado para "+mLookUpAddress, Toast.LENGTH_SHORT).show();
		}
	}

}
