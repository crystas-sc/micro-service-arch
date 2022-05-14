<script>
	import {
		Column,
		Content,
		Grid,
		Header,
		HeaderUtilities,
		Row,
		Select,
		SelectItem,
		SelectItemGroup,
	} from "carbon-components-svelte";
	import "carbon-components-svelte/css/all.css";
	import FileUploader from "./FileUploader.svelte";
	import ResultList from "./ResultList.svelte";

	let selected = "g90"; // "white" | "g10" | "g80" | "g90" | "g100"

	$: document.documentElement.setAttribute("theme", selected);
</script>

<Header company="CAPPS" platformName="3W Extractor">
	<HeaderUtilities>
		<div class="fit-content">
			<Select bind:selected>
				<SelectItemGroup label="Light theme">
					<SelectItem value="white" text="Theme White" />
					<SelectItem value="g10" text="Theme Gray 10" />
				</SelectItemGroup>
				<SelectItemGroup label="Dark theme">
					<SelectItem value="g80" text="Theme Gray 80" />
					<SelectItem value="g90" text="Theme Gray 90" />
					<SelectItem value="g100" text="Theme Gray 100" />
				</SelectItemGroup>
			</Select>
		</div>
	</HeaderUtilities>
</Header>

<Content>
	<Grid>
		<Row>
			<Column>
				<div class="center-content">
					<h1>3W Extractor</h1>
					<p>Who, When and Where Extractor</p>
				</div>
			</Column>
		</Row>
		<Row>
			<Column>
				<p class=" center-content margin-top3">
					<FileUploader />
				</p>
			</Column>
			<Column>
				<p class="center-content margin-top3">
					<ResultList />
				</p>
			</Column>
		</Row>
	</Grid>
</Content>

<style>
	.center-content {
		margin: 0 auto;
		width: fit-content;
		text-align: center;
	}
	.margin-top3 {
		margin-top: 3rem;
	}
	.fit-content {
		width: fit-content;
	}
</style>
