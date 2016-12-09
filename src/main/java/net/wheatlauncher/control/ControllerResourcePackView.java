package net.wheatlauncher.control;

/**
 * @author ci010
 */
public class ControllerResourcePackView
{
//	public StackPane root;
//	public JFXTreeTableView<ResCol> availableView;
//	public JFXTreeTableColumn<ResCol, StackPane> available;
//
//	public JFXTreeTableView<ResCol> selectedView;
//	public JFXTreeTableColumn<ResCol, StackPane> selected;
//
//	public JFXButton exportRes;
//	public JFXButton importRes;
//
//	@PostConstruct
//	public void init()
//	{
//		available.setCellValueFactory(createCallback(true));
//		selected.setCellValueFactory(createCallback(false));
//		refresh();
//		availableView.setRoot(new RecursiveTreeItem<>(avail, RecursiveTreeObject::getChildren));
//		selectedView.setRoot(new RecursiveTreeItem<>(using, RecursiveTreeObject::getChildren));
//	}
//
//	private ObservableList<ResCol> avail = FXCollections.observableArrayList(),
//			using = FXCollections.observableArrayList();

	private void refresh()
	{
//		LaunchProfile selectedProfile = Core.INSTANCE.getProfileManager().getSelectedProfile();
//		ResourcePackManager resourcePackManger = Core.INSTANCE.getResourcePackManger();
//		Set<ResourcePack> element = resourcePackManger.getAllIncludedElement(selectedProfile);
//		Set<ResourcePack> ava = resourcePackManger.getAllElement();
//		ava.removeAll(element);
//		using.clear();
//		using.addAll(element.stream().map(ResCol::new).collect(Collectors.toList()));
//		avail.clear();
//		avail.addAll(ava.stream().map(ResCol::new).collect(Collectors.toList()));
	}

//	private Callback<TreeTableColumn.CellDataFeatures<ResCol, StackPane>, ObservableValue<StackPane>> createCallback
//			(boolean left)
//	{
//		return (feature) ->
//		{
//			ResourcePackManager resourcePackManger = Core.INSTANCE.getResourcePackManger();
//
//			StackPane pane = new StackPane();
//
//			ResCol value = feature.getValue().getValue();
//			HBox back = new HBox();
//			{
//				try
//				{
//					ImageView icon = new ImageView((resourcePackManger).getIcon
//							(value.pack));
//					back.getChildren().add(icon);
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//				Label nameL = new Label(value.pack.getPackName()), desL = new Label(value.pack.getDescription());
//				back.getChildren().add(new VBox(nameL, desL));
//			}
//			pane.getChildren().add(back);
//			pane.getChildren().add(createBtnOverlay(left));
//
//			return new SimpleObjectProperty<>(pane);
//		};
//	}
//
//	private HBox createBtnOverlay(boolean left)
//	{
//		HBox btnRoot = new HBox();
//
//		JFXButton choose = new JFXButton();
//		if (left) choose.setGraphic(new Icon("caret-right"));
//		else choose.setGraphic(new Icon("caret-left"));
//
//		VBox moveBtnPanel = new VBox();
//		JFXButton moveUp = new JFXButton(), moveDown = new JFXButton();
//		moveUp.setGraphic(new Icon("caret-up"));
//		moveDown.setGraphic(new Icon("caret-down"));
//
//		moveBtnPanel.getChildren().add(moveUp);
//		moveBtnPanel.getChildren().add(moveDown);
//
//		if (left)
//		{
//			btnRoot.getChildren().add(moveBtnPanel);
//			btnRoot.getChildren().add(choose);
//		}
//		else
//		{
//			btnRoot.getChildren().add(choose);
//			btnRoot.getChildren().add(moveBtnPanel);
//		}
//		return btnRoot;
//	}
//
//	private class ResCol extends RecursiveTreeObject<ResCol>
//	{
//		private ResourcePack pack;
//
//		public ResCol(ResourcePack pack)
//		{
//			this.pack = pack;
//		}
//	}
}
