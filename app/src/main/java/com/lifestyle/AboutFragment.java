package com.lifestyle;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lifestyle.DAO.DatabaseHelper;
import com.lifestyle.licenses.CustomLicense;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuLesserGeneralPublicLicense21;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;

public class AboutFragment extends Fragment {


    private String name;
    private int id;
    private MainActivity mActivity = null;

    //Simple Switch case to handel button clicks
    private View.OnClickListener clickLicense = v -> {

        String name = null;
        String url = null;
        String copyright = null;
        License license = null;

        switch (v.getId()) {
            case R.id.MPAndroidChart:
                name = "";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.javaCSV:
                name = "JavaCSV";
                url = "";
                copyright = "";
                license = new GnuLesserGeneralPublicLicense21();
                break;
            case R.id.antoniomChronometer:
                name = "Millisecond-Chronometer";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.LicensesDialog:
                name = "LicensesDialog";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.PagerSlidingTabStrip:
                name = "PagerSlidingTabStrip";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.SmartTabLayout:
                name = "SmartTabLayout";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.flaticonCredits:
                name = "Flaticon";
                url = "";
                copyright = "";
                license = new CustomLicense("Free License (with attribution)", "");
                break;
            case R.id.freepikCredits:
                name = "Freepik";
                url = "";
                copyright = "";
                license = new CustomLicense("Free License (with attribution)", "");
                break;
            case R.id.CircleProgress:
                name = "CircleProgress";
                url = "https://github.com/lzyzsd/CircleProgress";
                copyright = "";
                license = new CustomLicense("WTFPL License", "");
                break;
            case R.id.CircularImageView:
                name = "CircularImageView";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.ktoast:
                name = "KToast";
                url = "https://github.com/onurkagan/KToast";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.SweetAlertDialog:
                name = "SweetAlertDialog";
                url = "";
                copyright = "";
                license = new MITLicense();
                break;
            case R.id.AndroidImageCropper:
                name = "Android-Image-Cropper";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
            case R.id.MaterialFavoriteButton:
                name = "Material Favorite Button";
                url = "";
                copyright = "";
                license = new ApacheSoftwareLicense20();
                break;
        }

        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(getMainActivity())
            .setNotices(notice)
            .build()
            .show();
    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static AboutFragment newInstance(String name, int id) {
        AboutFragment f = new AboutFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_about, container, false);

        //TextView pAppVersion = view.findViewById(R.id.app_version_textview);
        //pAppVersion.setText(); TODO get code version from Manifest

        TextView mpDBVersionTextView = view.findViewById(R.id.database_version);
        mpDBVersionTextView.setText(Integer.toString(DatabaseHelper.DATABASE_VERSION));

        TextView mpMPAndroidChartTextView = view.findViewById(R.id.MPAndroidChart);
        TextView mpJavaCVSTextView = view.findViewById(R.id.javaCSV);
        TextView mpLicenseDialogTextView = view.findViewById(R.id.LicensesDialog);
        TextView mpChronometerTextView = view.findViewById(R.id.antoniomChronometer);
        TextView mpPagerSlidingTabStripTextView = view.findViewById(R.id.PagerSlidingTabStrip);

        TextView mpSmartTabLayoutTextView = view.findViewById(R.id.SmartTabLayout);
        TextView mpFlaticonTextView = view.findViewById(R.id.flaticonCredits);
        TextView mpFreepikView = view.findViewById(R.id.freepikCredits);
        TextView mpCircleProgressView = view.findViewById(R.id.CircleProgress);
        TextView mpCircularImageView = view.findViewById(R.id.CircularImageView);
        TextView mpkToast = view.findViewById(R.id.ktoast);
        TextView mpSweetAlertDialog = view.findViewById(R.id.SweetAlertDialog);
        TextView mpAndroidImageCropper = view.findViewById(R.id.AndroidImageCropper);
        TextView mpMaterialFavoriteButton = view.findViewById(R.id.MaterialFavoriteButton);


        mpMPAndroidChartTextView.setOnClickListener(clickLicense);
        mpJavaCVSTextView.setOnClickListener(clickLicense);
        mpLicenseDialogTextView.setOnClickListener(clickLicense);
        mpChronometerTextView.setOnClickListener(clickLicense);
        mpPagerSlidingTabStripTextView.setOnClickListener(clickLicense);
        mpSmartTabLayoutTextView.setOnClickListener(clickLicense);
        mpFlaticonTextView.setOnClickListener(clickLicense);
        mpFreepikView.setOnClickListener(clickLicense);
        mpCircleProgressView.setOnClickListener(clickLicense);
        mpCircularImageView.setOnClickListener(clickLicense);
        mpkToast.setOnClickListener(clickLicense);
        mpSweetAlertDialog.setOnClickListener(clickLicense);
        mpAndroidImageCropper.setOnClickListener(clickLicense);
        mpMaterialFavoriteButton.setOnClickListener(clickLicense);

        // Inflate the layout for this fragment
        return view;
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

}

