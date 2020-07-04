# AllMediaFilePicker
Simple picker for all type of files (Image/Video/Files), manage with permision and callbacks

![alt text](https://raw.githubusercontent.com/bhoomit11/AllMediaFilePicker/master/images/ss.png)


This library handle all your callback from Camera gallery or file picker, also handles the permissions(CAMERA & WRITE_EXTERNAL_STORAGE) required for picker,
Also library allowes you to open your uplaods in full screen, and allow pinch to zoom for images and play for video and open you PDF files through picker

Follow below step to add this in to your project

Step 1. Add the dependency

   	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add it in your root build.gradle at the end of repositories:

    dependencies {
    	implementation 'com.github.bhoomit11:AllMediaFilePicker:1.0.1'
    }
  

Step 3. Add this view in your XML file
    
    <com.bb.allmediafilepicker.fileupload.FileUploadView
            android:id="@+id/fileUploadView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

Step 4. Initialize and configure

    binding.fileUploadView.setupWithActivityOrFragment(
            activity = this@FilePickerActivity,
    	    fragment = this@FilePickerFragment,
            fileUploadModel = FileUploadModel(
                title = "Attachments",
                minFilesCount = 1,           //
                maxFilesCount = 2,
                requiresCrop = false,
                requiresVideoCompress = true,
                mediaType = MediaPicker.MEDIA_TYPE_IMAGE or MediaPicker.MEDIA_TYPE_VIDEO,
                action = MediaPicker.ACTION_TYPE_CAMERA or MediaPicker.ACTION_TYPE_GALLERY
            )
        )

Provide Attributes in FileUploadModel()
Read below description to understand use of every attributes:

1.activity(Required): Activity/context in which you are using this view

2.fragment(Optional): Fragment if you are uding thi view in fragment, this use for getting the media callback after picking up the file, also NOTE: Activity is required in any case so if you using this in fragment do assign activity as fragment.getActivity()

3.fileUploadModel: Configuration model defines all configuration of a view
```
a.Title: Title of a view, as like title "Attachment" in screen shot above
b.minFilesCount: number of file you required minimum in your view
c.maxFilesCount: number of file you required maximum in your view, after maximu number of upload it won't allow you more
d.requiresCrop: give true if you want crop feature after image pick (Default false)
e.requiresVideoCompress: give true if you required video compress on video upload(Default false)
f.mediaType: this here is the main configuration, here in example `MediaPicker.MEDIA_TYPE_IMAGE or MediaPicker.MEDIA_TYPE_VIDEO` defines that you requre Image and Video both in this picker view,
if only image picker needed pass `MediaPicker.MEDIA_TYPE_IMAGE`, if file and Image pass `MediaPicker.MEDIA_TYPE_IMAGE or MediaPicker.MEDIA_TYPE_OTHER` and so on.
g.action: if you need only Camera pick then provide only `MediaPicker.ACTION_TYPE_CAMERA` here in example both camera and gallery needed so I pass both, In case of File pick pass `ACTION_TYPE_FILE`
```
  
Last step but not least:

    /**
     * To give media response back to file upload view
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.fileUploadView.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * To give media permission response back to file upload view
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.fileUploadView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

This 2 result are required by view to analyze respose from result for content data and Permissions response both
according the use, either in activity or in fragment

To open your uploaded data in full screen please define below activity in your project manifest:

 <!--Photo and Video Gallery full screen activity-->
        <activity
            android:name="com.bb.allmediafilepicker.utils.gallery.GalleryPagerActivity"
            android:configChanges="screenLayout|screenSize|orientation"
            android:screenOrientation="portrait"
            tools:ignore="LockedOrientationActivity" />
	    
	    
To change the color of picker ICONs you can override as below in you colors.xml file

    <color name="file_upload_colorPickerPrimary" tools:override="true">#03DAC5</color>
    <color name="file_upload_colorPickerSecondary" tools:override="true">#999999</color>
    
# License

    Copyright 2020 Bhoomit Belani

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
