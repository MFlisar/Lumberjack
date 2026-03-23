import UIKit
import MessageUI

#if canImport(UniformTypeIdentifiers)
import UniformTypeIdentifiers
#endif
#if canImport(MobileCoreServices)
import MobileCoreServices
#endif

@objc(MailSenderHelper)
@objcMembers
public final class MailSenderHelper: NSObject, MFMailComposeViewControllerDelegate {

    private var completion: ((Bool) -> Void)?

    public override init() {
        super.init()
    }

    // keep this signature
    public func sendMailWithReceiver(_ receiver: String, attachments: [String]) {
        guard MFMailComposeViewController.canSendMail() else {
            openMailFallback(to: receiver)
            return
        }

        guard let presenter = topViewController() else {
            return
        }

        let vc = MFMailComposeViewController()
        vc.mailComposeDelegate = self
        vc.setToRecipients([receiver])

        for path in attachments {
            addAttachment(from: path, to: vc)
        }

        presenter.present(vc, animated: true)
    }

    // MARK: - MFMailComposeViewControllerDelegate

    public func mailComposeController(_ controller: MFMailComposeViewController,
                                     didFinishWith result: MFMailComposeResult,
                                     error: Error?) {
        controller.dismiss(animated: true)
    }

    // MARK: - Helpers

    private func openMailFallback(to receiver: String) {
        let encoded = receiver.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? receiver
        guard let url = URL(string: "mailto:\(encoded)") else { return }
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

    private func addAttachment(from path: String, to mailVC: MFMailComposeViewController) {
        let url = URL(fileURLWithPath: path)

        guard let data = try? Data(contentsOf: url) else { return }

        let filename = url.lastPathComponent
        let ext = url.pathExtension.lowercased()

        let mime = mimeType(for: ext)
        mailVC.addAttachmentData(data, mimeType: mime, fileName: filename)
    }

    private func mimeType(for fileExtension: String) -> String {
        if fileExtension.isEmpty { return "application/octet-stream" }

        // iOS 14+
        #if canImport(UniformTypeIdentifiers)
        if #available(iOS 14.0, *) {
            if let ut = UTType(filenameExtension: fileExtension),
               let mime = ut.preferredMIMEType {
                return mime
            }
        }
        #endif

        // fallback (older iOS)
        #if canImport(MobileCoreServices)
        let ext = fileExtension as CFString
        if let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, ext, nil)?.takeRetainedValue(),
           let mime = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue() {
            return mime as String
        }
        #endif

        return "application/octet-stream"
    }

    private func topViewController() -> UIViewController? {
        guard let root = keyWindowRootViewController() else { return nil }
        return topMost(from: root)
    }

    private func keyWindowRootViewController() -> UIViewController? {
        if #available(iOS 13.0, *) {
            let scenes = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .filter { $0.activationState == .foregroundActive || $0.activationState == .foregroundInactive }

            for scene in scenes {
                if let window = scene.windows.first(where: { $0.isKeyWindow }),
                   let root = window.rootViewController {
                    return root
                }
            }

            // fallback: any visible window in foreground scene
            for scene in scenes {
                if let window = scene.windows.first(where: { !$0.isHidden }),
                   let root = window.rootViewController {
                    return root
                }
            }

            return UIApplication.shared.windows.first(where: { $0.isKeyWindow })?.rootViewController
        } else {
            return UIApplication.shared.keyWindow?.rootViewController
        }
    }

    private func topMost(from vc: UIViewController) -> UIViewController {
        if let presented = vc.presentedViewController {
            return topMost(from: presented)
        }
        if let nav = vc as? UINavigationController, let visible = nav.visibleViewController {
            return topMost(from: visible)
        }
        if let tab = vc as? UITabBarController, let selected = tab.selectedViewController {
            return topMost(from: selected)
        }
        return vc
    }
}
