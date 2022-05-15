<script>
    import {
        Column,
        FileUploader,
        Grid,
        Row,
        FileUploaderItem,
    } from "carbon-components-svelte";
    import { wDataStore } from "./store.js";

    let disabled = false,
        showImagePreview = false,
        isError = false,
        status = "uploading";
    let readImage, fileUploaderCmp, errMsg, lastUploadedFileName;

    const setImageData = (file) => {
        let reader = new FileReader();
        lastUploadedFileName = file.name;
        reader.readAsDataURL(file);
        reader.onload = (e) => {
            readImage = e.target.result;
            showImagePreview = true;
        };
    };

    const uploadFile = async (file) => {
        var data = new FormData();
        data.append("file", file);

        let res = await fetch(
            `https://${window.location.hostname}/ocr/upload/`,
            {
                method: "POST",
                body: data,
                mode: "cors",
            }
        );
        
        let resJson = await res.json();
        if(res.status != 200){
            throw resJson;
        }
        console.log("response", resJson);
        return resJson;
    };

</script>

<Grid>
    <Row>
        <Column>
            <div class="center-content">
                {#if isError}
                    <FileUploaderItem
                        invalid
                        id="file-upload-error"
                        name={lastUploadedFileName}
                        errorSubject="Error from network response"
                        errorBody={errMsg}
                        status="edit"
                        on:delete={() => {
                            isError = false;
                            showImagePreview = false;
                        }}
                    />
                {/if}
                <FileUploader
                    bind:this={fileUploaderCmp}
                    multiple={false}
                    labelTitle="Upload file"
                    buttonLabel="Add file"
                    labelDescription="Only Image files are accepted."
                    accept={[".jpg", ".jpeg", ".png"]}
                    {status}
                    {disabled}
                    on:add={async (e) => {
                        console.log("add", e);
                        status = "uploading";
                        $wDataStore = [];
                        showImagePreview = false;
                        isError = false;
                        let files = e.detail;
                        if (files.length > 0) {
                            disabled = true;
                            setImageData(files[0]);
                            try {
                                let res = await uploadFile(files[0]);
                                $wDataStore = res;
                            } catch (e) {
                                console.error(e);
                                isError = true;
                                errMsg = e.message || e.error || "Error in Network request";
                                fileUploaderCmp.clearFiles();
                            }
                            status = "complete";
                            disabled = false;
                        }
                    }}
                />
            </div>
        </Column>
    </Row>
    <Row>
        <Column>
            {#if showImagePreview}
                <img
                    class="uploaded-image"
                    width="100%"
                    src={readImage}
                    alt="Preview"
                />
            {/if}
        </Column>
    </Row>
</Grid>

<style>
    .center-content {
        margin: 0 auto;
        width: fit-content;
        text-align: center;
    }
</style>
