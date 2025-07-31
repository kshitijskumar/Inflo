import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init() {
        KoinHelper.shared.doInitKoin { _ in }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}