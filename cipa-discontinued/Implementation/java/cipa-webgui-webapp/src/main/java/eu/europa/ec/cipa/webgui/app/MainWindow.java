/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.webgui.app;

import java.io.File;
import java.io.FileNotFoundException;

import org.vaadin.jouni.animator.AnimatorProxy;


import com.phloc.appbasics.security.user.IUser;
import com.phloc.commons.io.resource.FileSystemResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.europa.ec.cipa.webgui.app.components.InvoiceTabForm;
import eu.europa.ec.cipa.webgui.app.components.InvoiceUploadWindow;
import eu.europa.ec.cipa.webgui.app.components.OrderUploadWindow;
import eu.europa.ec.cipa.webgui.app.login.UserFolder;
import eu.europa.ec.cipa.webgui.app.login.UserFolderManager;
import eu.europa.ec.cipa.webgui.app.utils.SendInvoice;

//import java.nio.file.Paths; //jdk1.7

/**
 * @author Jerouris
 */

public class MainWindow extends Window {

  private final CssLayout topBarCSSLayout = new CssLayout ();
  private final HorizontalLayout topBarLayout = new HorizontalLayout ();
  private final HorizontalLayout middleContentLayout = new HorizontalLayout ();
  private final HorizontalLayout footerLayout = new HorizontalLayout ();
  private HorizontalLayout topBarLayoutLeft;
  private HorizontalLayout topBarLayoutRight;
  private Component mainContentComponent;
  private final AnimatorProxy animProxy = new AnimatorProxy ();

  private final UserFolderManager <File> um;
  private ShowItemsPanel itemsPanel;

  public MainWindow () {
    super ("PAWG Main");
    addComponent (animProxy);
    um = PawgApp.getInstance ().getUserSpaceManager ();
    initUI ();
  }

  @SuppressWarnings ("serial")
  private void initUI () {

    final VerticalLayout root = new VerticalLayout ();
    root.setMargin (false);
    setContent (root);

    // createTopBar();
    // Changed with menuBar -- under testing
    createMenuBar ();
    // Changed with custom layout using bootstrap -- under testing
    // createHeaderMenu();

    final UserFolder <File> userFolder = new UserFolder <File> ();
    final long polling = 20000;
    final int draftInvoicesNum = um.countItemsInSpace (um.getDrafts ());
    final int inboxInvoicesNum = um.countItemsInSpace (um.getInbox ());
    final int outboxInvoicesNum = um.countItemsInSpace (um.getOutbox ());
    // Buttons
    final NativeButton inboxInvoices = new NativeButton ("Invoices (" + inboxInvoicesNum + ")");
    final NativeButton outboxInvoices = new NativeButton ("Invoices (" + outboxInvoicesNum + ")");
    final NativeButton draftInvoices = new NativeButton ("Invoices (" + draftInvoicesNum + ")");

    // thread
    final Thread tFolderCount = new Thread (new Runnable () {
      @Override
      public void run () {
        try {
          while (true) {
            final int countDrafts = um.countItemsInSpace (um.getDrafts ());
            final int countInbox = um.countItemsInSpace (um.getInbox ());
            final int countOutbox = um.countItemsInSpace (um.getOutbox ());
            synchronized (MainWindow.this.getApplication ()) {
              String labelD = draftInvoices.getCaption ();
              labelD = labelD.replaceFirst ("[\\d]+", "" + countDrafts);
              draftInvoices.setCaption (labelD);

              String labelI = inboxInvoices.getCaption ();
              labelI = labelI.replaceFirst ("[\\d]+", "" + countInbox);
              inboxInvoices.setCaption (labelI);

              String labelO = outboxInvoices.getCaption ();
              labelO = labelO.replaceFirst ("[\\d]+", "" + countOutbox);
              outboxInvoices.setCaption (labelO);

              itemsPanel.reloadTable (userFolder);
            }
            Thread.sleep (polling);
          }
        }
        catch (final InterruptedException e) {
          System.out.println ("Thread folders interrupted!!!");
        }
      }
    });

    // ------ START: Left NavBar -------
    final CssLayout leftNavBar = new CssLayout ();
    leftNavBar.setStyleName ("sidebar-menu");
    leftNavBar.setSizeFull ();
    leftNavBar.setWidth ("220px");

    // User theUser = (User) getApplication().getUser();
    final Label homeLbl = new Label ("HOME");
    homeLbl.addStyleName ("blue");
    leftNavBar.addComponent (homeLbl);

    leftNavBar.addComponent (new Label ("INBOX"));
    final NativeButton catalogueBtn = new NativeButton ("Catalogue");
    leftNavBar.addComponent (catalogueBtn);
    leftNavBar.addComponent (new NativeButton ("Orders"));
    // leftNavBar.addComponent (new NativeButton ("Invoices"));
    // int inboxInvoicesNum = um.countItemsInSpace(um.getInbox());
    // inboxInvoices = new NativeButton ("Invoices ("+inboxInvoicesNum+")");
    inboxInvoices.addListener (new ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        inboxInvoices.setCaption ("Invoices (" + um.countItemsInSpace (um.getInbox ()) + ")");
        userFolder.setFolder (um.getInbox ().getFolder ());
        userFolder.setName (um.getInbox ().getName ());
        showInitialMainContent (userFolder);
        draftInvoices.removeStyleName ("v-bold-nativebuttoncaption");
      }
    });
    leftNavBar.addComponent (inboxInvoices);

    leftNavBar.addComponent (new Label ("DRAFTS"));
    leftNavBar.addComponent (new NativeButton ("Catalogue"));
    leftNavBar.addComponent (new NativeButton ("Orders"));
    // int draftInvoicesNum = um.countItemsInSpace(um.getDrafts());
    // draftInvoices = new NativeButton ("Invoices ("+draftInvoicesNum+")");
    draftInvoices.addListener (new ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        draftInvoices.setCaption ("Invoices (" + um.countItemsInSpace (um.getDrafts ()) + ")");
        userFolder.setFolder (um.getDrafts ().getFolder ());
        userFolder.setName (um.getDrafts ().getName ());
        showInitialMainContent (userFolder);
        draftInvoices.removeStyleName ("v-bold-nativebuttoncaption");
      }
    });
    leftNavBar.addComponent (draftInvoices);

    leftNavBar.addComponent (new Label ("OUTBOX"));
    leftNavBar.addComponent (new NativeButton ("Catalogue"));
    leftNavBar.addComponent (new NativeButton ("Orders"));
    // leftNavBar.addComponent (new NativeButton ("Invoices"));
    // int outboxInvoicesNum = um.countItemsInSpace(um.getOutbox());
    // outboxInvoices = new NativeButton ("Invoices ("+outboxInvoicesNum+")");
    outboxInvoices.addListener (new ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        outboxInvoices.setCaption ("Invoices (" + um.countItemsInSpace (um.getOutbox ()) + ")");
        userFolder.setFolder (um.getOutbox ().getFolder ());
        userFolder.setName (um.getOutbox ().getName ());
        showInitialMainContent (userFolder);
        draftInvoices.removeStyleName ("v-bold-nativebuttoncaption");
      }
    });
    leftNavBar.addComponent (outboxInvoices);

    leftNavBar.addComponent (new Label ("SETTINGS"));
    leftNavBar.addComponent (new NativeButton ("My Profile"));
    leftNavBar.addComponent (new NativeButton ("Customers"));
    leftNavBar.addComponent (new NativeButton ("Suppliers"));

    final Embedded peppolLogoImg = new Embedded (null, new ExternalResource ("img/peppol_logo.png"));

    peppolLogoImg.setStyleName ("logo");
    leftNavBar.addComponent (peppolLogoImg);

    middleContentLayout.addComponent (leftNavBar);

    /*
     * Button refreshButton = new Button("Refresh");
     * refreshButton.addListener(new Button.ClickListener() {
     * @Override public void buttonClick(ClickEvent event) { int draftInvoices =
     * um.countItemsInSpace(um.getDrafts());
     * invoices.setCaption("Invoices ("+draftInvoices+")"); } });
     * leftNavBar.addComponent(refreshButton);
     */

    // workaround so that thread refreshes UI. It seems that when a
    // ProgressIndicator is present,
    // all components receive server side refreshes
    final ProgressIndicator p = new ProgressIndicator ();
    p.setPollingInterval ((int) polling);
    p.setWidth ("0px");
    p.setHeight ("0px");
    leftNavBar.addComponent (p);

    showInitialMainContent (null);
    draftInvoices.click ();
    tFolderCount.start ();
    draftInvoices.addStyleName ("v-bold-nativebuttoncaption");
  }

  public void showInitialMainContent (final UserFolder <?> userFolder) {
    // ------ START: Main Content -------
    final VerticalLayout mainContentLayout = new VerticalLayout ();

    mainContentLayout.addStyleName ("margin");
    final VerticalLayout topmain = new VerticalLayout ();
    topmain.setSpacing (true);
    topmain.setWidth ("100%");
    final Label bigPAWGLabel = new Label ("PEPPOL Post Award Web GUI");
    bigPAWGLabel.setStyleName ("huge");
    topmain.addComponent (bigPAWGLabel);
    final Label blahContent = new Label ("This is a mockup of the GUI that is going"
                                         + " to be the PAWG. It is created by the Greek"
                                         + " and Austrian teams as a fine replacement "
                                         + " of the Demo Client");
    blahContent.setWidth ("80%");
    blahContent.addStyleName ("big");
    // topmain.addComponent (blahContent);
    // HorizontalLayout itemsPanel = new ShowItemsPanel("Items", um,
    // userFolder);

    final ShowItemsPanel itemsPanel = new ShowItemsPanel ("Items", um, userFolder);
    this.itemsPanel = itemsPanel;
    topmain.addComponent (itemsPanel);
    final HorizontalLayout buttonsLayout = new HorizontalLayout ();
    buttonsLayout.setSpacing (true);
    topmain.addComponent (buttonsLayout);
    final Button loadButton = new Button ("Load invoice");
    // topmain.addComponent(loadButton);
    buttonsLayout.addComponent (loadButton);
    loadButton.addListener (new ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        final Table table = itemsPanel.getTable ();
        if (table.getValue () != null) {
          // InvoiceType inv =
          // (InvoiceType)table.getItem(table.getValue()).getItemProperty("invoice").getValue();
          // InvoiceBean invBean = (InvoiceBean)table.getItem(table.getValue());
          final InvoiceBean invBean = ((InvoiceBeanContainer) table.getContainerDataSource ()).getItem (table.getValue ())
                                                                                              .getBean ();
          // System.out.println("Invoice is: "+invBean);
          showInvoiceForm (invBean);
        }
      }
    });

    final Button sendButton = new Button ("Send invoice");
    buttonsLayout.addComponent (sendButton);
    sendButton.addListener (new ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        try {
          final Table table = itemsPanel.getTable ();
          if (table.getValue () != null) {
            final InvoiceBean invBean = ((InvoiceBeanContainer) table.getContainerDataSource ()).getItem (table.getValue ())
                                                                                                .getBean ();
            final String path = invBean.getFolderEntryID ();
            final FileSystemResource s = new FileSystemResource (path);
            SendInvoice.sendDocument (s);

            // file is sent. move invoice to outbox
            um.moveInvoice (invBean, um.getDrafts (), um.getOutbox ());
            // itemsPanel.getTable().requestRepaint();
            itemsPanel.init (um.getDrafts ());
          }
        }
        catch (final FileNotFoundException e) {
          getWindow ().showNotification ("Could not find invoice file", Notification.TYPE_ERROR_MESSAGE);
        }
        catch (final Exception e) {
          getWindow ().showNotification ("Could not send invoice. AP connection error", Notification.TYPE_ERROR_MESSAGE);
          e.printStackTrace ();
        }
      }
    });

    final Button learnMoreBtn = new Button ("Learn More >>");
    learnMoreBtn.addStyleName ("tall default");
    // topmain.addComponent (learnMoreBtn);

    mainContentLayout.addComponent (topmain);
    // ------ END: Main Content ---------
    mainContentLayout.setHeight ("100%");
    mainContentLayout.setSizeFull ();

    mainContentLayout.setSpacing (true);
    mainContentLayout.setWidth ("100%");
    middleContentLayout.setWidth ("100%");
    middleContentLayout.setHeight ("100%");
    middleContentLayout.setMargin (true);
    // --------
    addComponent (middleContentLayout);
    addComponent (footerLayout);
    if (mainContentComponent != null) {
      middleContentLayout.replaceComponent (mainContentComponent, mainContentLayout);
    }
    else {
      middleContentLayout.addComponent (mainContentLayout);
    }
    middleContentLayout.setExpandRatio (mainContentLayout, 1);
    mainContentComponent = mainContentLayout;
  }

  @SuppressWarnings ("unused")
  private void createTopBar () {

    topBarCSSLayout.setStyleName ("toolbar");
    topBarCSSLayout.setSizeFull ();
    topBarLayout.setMargin (false, true, false, true);
    topBarLayout.setSizeFull ();
    topBarLayoutLeft = new HorizontalLayout ();
    topBarLayoutRight = new HorizontalLayout ();

    final Label pawgLabel = new Label ("PAWG");
    pawgLabel.setStyleName ("h1");
    pawgLabel.setSizeUndefined ();
    topBarLayoutLeft.addComponent (pawgLabel);

    final HorizontalLayout segBtns = createTopBarButtons ();
    topBarLayoutLeft.addComponent (segBtns);

    // IUser user = (IUser) PawgApp.getInstance().getUser();
    final Label loggedInLabel = new Label ("Test User");
    loggedInLabel.setSizeUndefined ();
    topBarLayoutRight.addComponent (loggedInLabel);
    topBarLayoutRight.setComponentAlignment (loggedInLabel, Alignment.MIDDLE_RIGHT);
    topBarLayoutLeft.setComponentAlignment (segBtns, Alignment.MIDDLE_CENTER);
    topBarLayoutLeft.setSpacing (true);

    topBarLayout.addComponent (topBarLayoutLeft);
    topBarLayout.addComponent (topBarLayoutRight);
    topBarLayout.setComponentAlignment (topBarLayoutRight, Alignment.MIDDLE_RIGHT);

    topBarLayout.setExpandRatio (topBarLayoutLeft, 1);
    topBarLayout.setExpandRatio (topBarLayoutRight, 1);

    topBarCSSLayout.addComponent (topBarLayout);
    addComponent (topBarCSSLayout);

  }

  private void createMenuBar () {

    topBarLayout.setMargin (false, false, false, false);
    topBarLayout.setSizeFull ();
    // topBarLayout.setStyleName("v-menubar");
    topBarLayoutLeft = new HorizontalLayout ();
    topBarLayoutRight = new HorizontalLayout ();

    // Label pawgLabel = new Label("PAWG",Label.CONTENT_XHTML);
    // pawgLabel.setStyleName("v-menubar");
    // pawgLabel.addStyleName("v-label-big");
    // pawgLabel.setSizeFull();
    // topBarLayoutLeft.addComponent(pawgLabel);

    final MenuBar lMenuBar = new MenuBar ();
    lMenuBar.setHtmlContentAllowed (true);
    lMenuBar.addItem ("<b>PAWG<b>", new MenuBar.Command () {

      @Override
      public void menuSelected (final MenuItem selectedItem) {

        removeComponent (mainContentComponent);
        showInitialMainContent (um.getDrafts ());
      }
    });
    final MenuBar.MenuItem docItem = lMenuBar.addItem ("Document", null);
    lMenuBar.addItem ("Preferences", null);
    lMenuBar.addItem ("Logout", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        PawgApp.getInstance ().logout ();
        PawgApp.getInstance ().showLoginWindow ();
      }
    });
    lMenuBar.addItem ("About", null);
    lMenuBar.setSizeFull ();

    final MenuBar.MenuItem invItem = docItem.addItem ("Invoice", null);
    final MenuBar.MenuItem orderItem = docItem.addItem ("Order", null);
    invItem.addItem ("New ...", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        showInvoiceForm ();
        // showInvoiceForm (null);
      }
    });
    invItem.addItem ("View ... ", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        showTestForm ();
      }
    });
    invItem.addItem ("Upload ...", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        showInvUploadWindow ();
      }
    });

    orderItem.addItem ("New", null);
    orderItem.addItem ("View", null);
    orderItem.addItem ("Upload ...", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        showOrdUploadWindow ();
      }
    });

    topBarLayoutLeft.addComponent (lMenuBar);

    final IUser user = (IUser) PawgApp.getInstance ().getUser ();

    topBarLayoutLeft.setComponentAlignment (lMenuBar, Alignment.MIDDLE_CENTER);
    topBarLayoutLeft.setSpacing (false);
    topBarLayoutLeft.setSizeFull ();
    topBarLayoutRight.setSizeUndefined ();

    final MenuBar rMenuBar = new MenuBar ();
    rMenuBar.setHtmlContentAllowed (true);
    final MenuBar.MenuItem userLabel = rMenuBar.addItem ("<b>" + user.getEmailAddress () + "<b>", null);
    userLabel.addItem ("Logout", new MenuBar.Command () {
      @Override
      public void menuSelected (final MenuItem selectedItem) {
        PawgApp.getInstance ().logout ();
      }
    });
    topBarLayoutRight.addComponent (rMenuBar);
    topBarLayout.addComponent (topBarLayoutLeft);
    topBarLayout.addComponent (topBarLayoutRight);
    topBarLayout.setComponentAlignment (topBarLayoutRight, Alignment.MIDDLE_RIGHT);
    topBarLayout.setExpandRatio (topBarLayoutLeft, 1);
    // topBarLayout.setExpandRatio(topBarLayoutRight, 1);
    addComponent (topBarLayout);

  }

  @SuppressWarnings ("unused")
  private void createHeaderMenu () {
    topBarLayout.setMargin (false, false, false, false);
    topBarLayout.setSizeFull ();
    final CustomLayout custom = new CustomLayout ("header-menu");
    topBarLayout.addComponent (custom);

    // Button ok = new Button("Login");
    // ok.removeStyleName ("v-button-wrap");
    // ok.addStyleName ("btn btn-success");
    // custom.addComponent(ok, "okbutton");

    addComponent (topBarLayout);
  }

  private HorizontalLayout createTopBarButtons () {

    final HorizontalLayout topBarBtns = new HorizontalLayout ();

    final Button homeBtn = new Button ("Home");
    homeBtn.addStyleName ("first");
    homeBtn.addStyleName ("down");

    final Button aboutBtn = new Button ("About");
    final Button contactBtn = new Button ("Contact");
    contactBtn.addStyleName ("last");

    topBarBtns.setStyleName ("segment");
    topBarBtns.addStyleName ("tall");
    topBarBtns.addComponent (homeBtn);
    topBarBtns.addComponent (aboutBtn);
    topBarBtns.addComponent (contactBtn);

    return topBarBtns;
  }

  @SuppressWarnings ("unused")
  private static String getLoremIpsum () {

    final String lorem = "Sed ultrices, est dapibus aliquet interdum, sapien "
                         + "sapien elementum diam, sed tempus odio mi vitae felis. Donec"
                         + " et tortor ipsum. Pellentesque est est, feugiat tincidunt "
                         + "volutpat sed, facilisis ac est. Vestibulum convallis orci vel"
                         + " justo hendrerit ac vulputate urna ornare. Pellentesque semper"
                         + " consectetur tortor, eu egestas enim fringilla volutpat. "
                         + "Donec blandit congue tellus, at faucibus erat luctus rutrum."
                         + " Phasellus rhoncus turpis ut orci ornare vehicula. Etiam sem"
                         + " neque, dictum ac commodo nec, molestie non enim. Aliquam "
                         + "egestas sem eget sapien pellentesque vel scelerisque risus"
                         + " semper. Vivamus ac nisi turpis, sit amet sagittis elit.";

    return lorem;
  }

  public void showInvoiceForm () {

    // InvoiceForm invForm = new InvoiceForm();
    // final InvoiceTabForm invForm = new InvoiceTabForm ();
    final InvoiceTabForm invForm = new InvoiceTabForm (um);
    middleContentLayout.replaceComponent (mainContentComponent, invForm);
    middleContentLayout.setExpandRatio (invForm, 1);
    mainContentComponent = invForm;
  }

  public void showInvoiceForm (final InvoiceBean bean) {
    final InvoiceTabForm invForm = new InvoiceTabForm (um, bean);
    middleContentLayout.replaceComponent (mainContentComponent, invForm);
    middleContentLayout.setExpandRatio (invForm, 1);
    mainContentComponent = invForm;
  }

  public void showTestForm () {

    // InvoiceForm form = new InvoiceForm();
    // Form f2 = form.createInvoiceTopForm();
    // middleContentLayout.replaceComponent(mainContentComponent,f2);
    // middleContentLayout.setExpandRatio(f2, 1);
    // mainContentComponent = f2;
  }

  public void showInvUploadWindow () {
    // this.showNotification("Warning",
    // "<br/>Uploading invoices is under construction",
    // Window.Notification.TYPE_HUMANIZED_MESSAGE);
    final Window popup = new InvoiceUploadWindow ().getWindow ();
    popup.setResizable (false);
    popup.setHeight ("150px");
    popup.setWidth ("430px");
    getWindow ().addWindow (popup);
  }

  public void showOrdUploadWindow () {
    // this.showNotification("Warning",
    // "<br/>Uploading orders is under construction",
    // Window.Notification.TYPE_HUMANIZED_MESSAGE);
    final Window popup = new OrderUploadWindow ().getWindow ();
    popup.setResizable (false);
    popup.setHeight ("150px");
    popup.setWidth ("430px");
    getWindow ().addWindow (popup);
  }

}
