<script>
    import { Column, FileUploader, Grid, Row } from "carbon-components-svelte";
    import { wDataStore } from "./store.js";

    let disabled = false,
        showImagePreview = false,
        status = "uploading";
    let readImage;

    const setImageData = (file) => {
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = (e) => {
            readImage = e.target.result;
            showImagePreview = true;
        };
    };

    const uploadFile = async (file) => {
        var data = new FormData();
        data.append("file", file);

        let res = await fetch("http://ocr", {
            method: "POST",
            body: data,
            mode: "cors",
        });
        res = res.json();
        console.log("response", res);
        return res;
    };

    // const disptachData = () =>{
    //     let data = [{"label":"GPE","text":"Carlisle"},{"label":"GPE","text":"Carlisle"},{"label":"PERSON","text":"Edward"},{"label":"GPE","text":"Carlisle"},{"label":"GPE","text":"Carlisle"},{"label":"PERSON","text":"Bella"},{"label":"PERSON","text":"Edward"},{"label":"PERSON","text":"Edward"}];
    //     $wDataStore = data;

    //     status="complete";
    // }
</script>

<Grid>
    <Row>
        <Column>
            <div class="center-content">
                <FileUploader
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
                        let files = e.detail;
                        if (files.length > 0) {
                            disabled = true;
                            setImageData(files[0]);
                            let res = await uploadFile(files[0]);
                            $wDataStore = res;
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
