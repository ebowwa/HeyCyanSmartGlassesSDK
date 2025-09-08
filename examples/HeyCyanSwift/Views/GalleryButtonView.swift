//
//  GalleryButtonView.swift
//  HeyCyanSwift
//
//  Gallery navigation button component
//

import SwiftUI

struct GalleryButtonView: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: "photo.stack")
                Text("AI Gallery")
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(.secondary)
            }
            .padding()
            .background(Color(UIColor.systemGray6))
            .cornerRadius(10)
        }
        .padding(.horizontal)
        .padding(.top)
    }
}

#Preview {
    GalleryButtonView(action: {})
}