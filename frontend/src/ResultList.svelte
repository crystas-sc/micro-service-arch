<script>
    import { Column, Grid, Row } from "carbon-components-svelte";
    import { wDataStore } from './store.js' 

    let who =[], when = [],where = [];
    $:{
        where = $wDataStore?.filter((x) => x.label == "GPE").map((x) => x.text) || [];
        when = $wDataStore?.filter((x) => x.label == "DATE").map((x) => x.text) || [];
        who = $wDataStore?.filter((x) => x.label == "PERSON" || x.label == "ORG").map((x) => x.text) || [];
    }
   
</script>
<style>
    .header{
        
        min-width: 10rem;
        border-bottom: 2px solid var(--cds-ui-03, #e0e0e0)
    }
    ul{
        list-style: inside;
        text-align: left;
    }
</style>

<Grid >
    <Row>
        <Column>
            <h3 class="header">Who</h3>
            <ul>
                {#each who as item}
                    <li>{item}</li>
                {/each}
            </ul>
        </Column>
        <Column>
            <h3 class="header">When</h3>
            <ul>
                {#each when as item}
                    <li>{item}</li>
                {/each}
            </ul>
        </Column>
        <Column>
            <h3 class="header">Where</h3>
            <ul>
                {#each where as item}
                    <li>{item}</li>
                {/each}
            </ul>
        </Column>
    </Row>
</Grid>
