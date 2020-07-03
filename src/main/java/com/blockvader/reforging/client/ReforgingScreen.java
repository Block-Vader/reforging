package com.blockvader.reforging.client;

import com.blockvader.reforging.Reforging;
import com.blockvader.reforging.ReforgingContainer;
import com.blockvader.reforging.network.ReforgingPacketHandler;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

public class ReforgingScreen extends ContainerScreen<ReforgingContainer>{
	
	private static final ResourceLocation REFORGING_TABLE = new ResourceLocation(Reforging.MOD_ID + ":textures/gui/container/reforging_table.png");
	
	private ReforgingContainer container;
	private ImageButton damageUp;
	private ImageButton damageDown;
	private ImageButton speedUp;
	private ImageButton speedDown;
	private ImageButton knockbackUp;
	private ImageButton knockbackDown;
	private ImageButton reachUp;
	private ImageButton reachDown;
	private ImageButton specialUp;
	private ImageButton specialDown;
	private ImageButton confirm;
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		//if (container.getStats().size() >= 4) System.out.println("Attack: " + container.getStats().get(0) + ", Speed: " + container.getStats().get(1) + ", Knockback: " + container.getStats().get(2) + ", Reach: " + container.getStats().get(3));
		String s = this.title.getFormattedText();
		this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
		if (container.canReforge())
		{
			double dmg = Math.round(this.container.getStats()[0] * 10)/10.0D;
			this.font.drawString(Double.toString(dmg), (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2) + 30.5F, 22.0F, 4210752);
			double spd = Math.round(this.container.getStats()[1] * 10)/10.0D;
			this.font.drawString(Double.toString(spd), (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2) + 30.5F, 44.0F, 4210752);
			double knockback = Math.round(this.container.getStats()[2] * 10)/10.0D;
			this.font.drawString(Double.toString(knockback), (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2) + 30.5F, 66.0F, 4210752);
			double reach = Math.round(this.container.getStats()[3] * 10)/10.0D;
			this.font.drawString(Double.toString(reach), (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2) + 30.5F, 88.0F, 4210752);
			double special = Math.round(this.container.getStats()[4] * 10)/10.0D;
			this.font.drawString(Double.toString(special), (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2) + 30.5F, 110.0F, 4210752);
			this.font.drawString("Damage", 8.0F, 22.0F, 4210752);
			this.font.drawString("Speed", 8.0F, 44.0F, 4210752);
			this.font.drawString("Knockback", 8.0F, 66.0F, 4210752);
			this.font.drawString("Reach", 8.0F, 88.0F, 4210752);
			ItemStack stack = container.getSlot(0).getStack();
			if (stack.getItem() instanceof SwordItem) this.font.drawString("Sweep Area", 8.0F, 110.0F, 4210752);
			else if (stack.getItem() instanceof AxeItem) this.font.drawString("Shield Break", 8.0F, 110.0F, 4210752);
			else if (stack.getItem() instanceof TridentItem) this.font.drawString("Aerodynamic", 8.0F, 110.0F, 4210752);
		}
	}

	public ReforgingScreen(ReforgingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		this.xSize = 176;
		this.ySize = 220;
		this.container = screenContainer;
	}
	
	@Override
	protected void init() {
		this.damageUp = new ImageButton(0, 0, 14, 14, 176, 0, 14, REFORGING_TABLE, (button) -> {
			container.statUp(0);
		});
		this.damageDown = new ImageButton(0, 0, 14, 14, 190, 0, 14, REFORGING_TABLE, (button) -> {
			container.statDown(0);
		});
		this.speedUp = new ImageButton(0, 0, 14, 14, 176, 0, 14, REFORGING_TABLE, (button) -> {
			container.statUp(1);
		});
		this.speedDown = new ImageButton(0, 0, 14, 14, 190, 0, 14, REFORGING_TABLE, (button) -> {
			container.statDown(1);
		});
		this.knockbackUp = new ImageButton(0, 0, 14, 14, 176, 0, 14, REFORGING_TABLE, (button) -> {
			container.statUp(2);
		});
		this.knockbackDown = new ImageButton(0, 0, 14, 14, 190, 0, 14, REFORGING_TABLE, (button) -> {
			container.statDown(2);
		});
		this.reachUp = new ImageButton(0, 0, 14, 14, 176, 0, 14, REFORGING_TABLE, (button) -> {
			container.statUp(3);
		});
		this.reachDown = new ImageButton(0, 0, 14, 14, 190, 0, 14, REFORGING_TABLE, (button) -> {
			container.statDown(3);
		});
		this.specialUp = new ImageButton(0, 0, 14, 14, 176, 0, 14, REFORGING_TABLE, (button) -> {
			container.statUp(4);
		});
		this.specialDown = new ImageButton(0, 0, 14, 14, 190, 0, 14, REFORGING_TABLE, (button) -> {
			container.statDown(4);
		});
		this.confirm = new ImageButton(0, 0, 20, 20, 176, 40, 20, REFORGING_TABLE, (button) -> {
			ReforgingPacketHandler.INSTANCE.sendToServer(new ReforgingPacketHandler.ReforgePacket(container.windowId, container.getStatChanges()));
			Minecraft.getInstance().world.playSound(Minecraft.getInstance().player.getPosition(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
		});
		this.addButton(damageUp);
		this.addButton(damageDown);
		this.addButton(speedUp);
		this.addButton(speedDown);
		this.addButton(knockbackUp);
		this.addButton(knockbackDown);
		this.addButton(reachUp);
		this.addButton(reachDown);
		this.addButton(specialUp);
		this.addButton(specialDown);
		this.addButton(confirm);
		super.init();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		//System.out.println(container.canDown(0));
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(REFORGING_TABLE);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		if (container.canReforge())
		{
			this.damageUp.visible = container.canUp(0);
			this.damageDown.visible = container.canDown(0);
			this.speedUp.visible = container.canUp(1);
			this.speedDown.visible = container.canDown(1);
			this.knockbackUp.visible = container.canUp(2);
			this.knockbackDown.visible = container.canDown(2);
			this.reachUp.visible = container.canUp(3);
			this.reachDown.visible = container.canDown(3);
			this.specialUp.visible = container.canUp(4);
			this.specialDown.visible = container.canDown(4);
			if (container.enoughResources() && container.getUps() == container.getDowns())
			{
				this.confirm.visible = true;
				this.confirm.setPosition(i + 141, j + 61);
			}
			else confirm.visible = false;
			
			this.damageUp.setPosition(i + 115, j + 19);
			this.damageDown.setPosition(i + 73, j + 19);
			this.speedUp.setPosition(i + 115, j + 41);
			this.speedDown.setPosition(i + 73, j + 41);
			this.knockbackUp.setPosition(i + 115, j + 63);
			this.knockbackDown.setPosition(i + 73, j + 63);
			this.reachUp.setPosition(i + 115, j + 85);
			this.reachDown.setPosition(i + 73, j + 85);
			this.specialUp.setPosition(i + 115, j + 107);
			this.specialDown.setPosition(i + 73, j + 107);
			/*
			this.blit(i+115, j+19, 176, 0, 14, 14);
			this.blit(i+115, j+41, 176, 0, 14, 14);
			this.blit(i+115, j+63, 176, 0, 14, 14);
			this.blit(i+115, j+85, 176, 0, 14, 14);
			this.blit(i+115, j+107, 176, 0, 14, 14);
			
			this.blit(i+73, j+19, 190, 0, 14, 14);
			this.blit(i+73, j+41, 190, 0, 14, 14);
			this.blit(i+73, j+63, 190, 0, 14, 14);
			this.blit(i+73, j+85, 190, 0, 14, 14);
			this.blit(i+73, j+107, 190, 0, 14, 14);
			*/
			
			this.blit(i+89, j+20, 176, 28, 24, 12);
			this.blit(i+89, j+42, 176, 28, 24, 12);
			this.blit(i+89, j+64, 176, 28, 24, 12);
			this.blit(i+89, j+86, 176, 28, 24, 12);
			this.blit(i+89, j+108, 176, 28, 24, 12);
		}
		else {
			this.damageUp.visible = false;
			this.damageDown.visible = false;
			this.speedUp.visible = false;
			this.speedDown.visible = false;
			this.knockbackUp.visible = false;
			this.knockbackDown.visible = false;
			this.reachUp.visible = false;
			this.reachDown.visible = false;
			this.specialUp.visible = false;
			this.specialDown.visible = false;
			this.confirm.visible = false;
		}
	}
	
	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_)
	{
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.renderHoveredToolTip(p_render_1_, p_render_2_);
	}

}
